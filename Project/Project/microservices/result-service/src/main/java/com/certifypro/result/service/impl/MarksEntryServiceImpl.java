package com.certifypro.result.service.impl;

import com.certifypro.result.client.NotificationServiceGateway;
import com.certifypro.result.client.QuestionServiceGateway;
import com.certifypro.result.client.dto.PaperDto;
import com.certifypro.result.common.MarksStatus;
import com.certifypro.result.common.ScriptStatus;
import com.certifypro.result.dto.request.CreateMarksEntryRequest;
import com.certifypro.result.dto.response.MarksEntryResponse;
import com.certifypro.result.dto.response.PageResponse;
import com.certifypro.result.entity.MarksEntry;
import com.certifypro.result.entity.ScriptAllocation;
import com.certifypro.result.exception.BusinessException;
import com.certifypro.result.exception.NotFoundException;
import com.certifypro.result.repository.MarksEntryRepository;
import com.certifypro.result.repository.ScriptAllocationRepository;
import com.certifypro.result.security.SecurityUtil;
import com.certifypro.result.service.MarksEntryService;
import com.certifypro.result.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MarksEntryServiceImpl implements MarksEntryService {

    private final MarksEntryRepository marksRepository;
    private final ScriptAllocationRepository scriptRepository;
    private final QuestionServiceGateway questionGateway;
    private final NotificationServiceGateway notificationGateway;

    public MarksEntryServiceImpl(MarksEntryRepository marksRepository,
                                 ScriptAllocationRepository scriptRepository,
                                 QuestionServiceGateway questionGateway,
                                 NotificationServiceGateway notificationGateway) {
        this.marksRepository = marksRepository;
        this.scriptRepository = scriptRepository;
        this.questionGateway = questionGateway;
        this.notificationGateway = notificationGateway;
    }

    /** Dual marking: max two entries per script; flag for moderation when they differ &gt; 10% of total. */
    @Override
    @Transactional
    public MarksEntryResponse submit(CreateMarksEntryRequest req) {
        ScriptAllocation script = scriptRepository.findById(req.scriptId())
                .orElseThrow(() -> NotFoundException.of("ScriptAllocation", req.scriptId()));

        long existing = marksRepository.countByScript_ScriptId(req.scriptId());
        if (existing >= 2) {
            throw new BusinessException("This script already has two marks entries (dual marking complete)");
        }

        MarksEntry entry = MarksEntry.builder()
                .script(script)
                .evaluatorId(SecurityUtil.currentUserId())
                .marksAwarded(req.marksAwarded())
                .entryDate(LocalDate.now())
                .status(MarksStatus.Submitted)
                .build();
        entry = marksRepository.save(entry);

        if (existing + 1 == 2) {
            evaluateDualMarking(script);
        } else {
            script.setStatus(ScriptStatus.UnderEvaluation);
            scriptRepository.save(script);
        }

        return MarksEntryResponse.from(entry);
    }

    private void evaluateDualMarking(ScriptAllocation script) {
        List<MarksEntry> entries = marksRepository.findByScript_ScriptId(script.getScriptId());

        // Paper total marks now come from question-service via a circuit-breaker gateway
        // (was a local QuestionPaperRepository lookup in the monolith). If question-service
        // is unavailable the total degrades to 0, which disables the moderation threshold
        // check (same effect the monolith had for a missing/null paper total).
        PaperDto paper = questionGateway.getPaper(script.getPaperId());
        int total = (paper == null || paper.totalMarks() == null) ? 0 : paper.totalMarks();

        int diff = Math.abs(entries.get(0).getMarksAwarded() - entries.get(1).getMarksAwarded());
        double threshold = 0.10 * total;

        if (total > 0 && diff > threshold) {
            // Flag both entries for moderation and notify the Exam Controllers.
            for (MarksEntry e : entries) {
                e.setStatus(MarksStatus.Moderated);
                marksRepository.save(e);
            }
            notificationGateway.notifyRole("ExamController", "Result",
                    "Script #" + script.getScriptId() + " flagged for moderation: dual-mark difference "
                            + diff + " exceeds 10% of " + total);
        }

        script.setStatus(ScriptStatus.MarksSubmitted);
        scriptRepository.save(script);
    }

    @Override
    public PageResponse<MarksEntryResponse> list(String status, Long scriptId, int page, int limit) {
        if (scriptId != null) {
            List<MarksEntryResponse> list = marksRepository.findByScript_ScriptId(scriptId)
                    .stream().map(MarksEntryResponse::from).toList();
            return new PageResponse<>(list, 0, list.size(), list.size(), 1, true);
        }
        MarksStatus st = status == null ? MarksStatus.Moderated : MarksStatus.valueOf(status);
        return PageResponse.from(
                marksRepository.findByStatus(st, PageUtil.of(page, limit))
                        .map(MarksEntryResponse::from));
    }

    @Override
    @Transactional
    public MarksEntryResponse verify(Long id) {
        MarksEntry e = find(id);
        e.setStatus(MarksStatus.Verified);
        e.setVerifiedById(SecurityUtil.currentUserId());
        e = marksRepository.save(e);
        return MarksEntryResponse.from(e);
    }

    @Override
    @Transactional
    public MarksEntryResponse moderate(Long id) {
        MarksEntry e = find(id);
        e.setStatus(MarksStatus.Moderated);
        e.setVerifiedById(SecurityUtil.currentUserId());
        e = marksRepository.save(e);
        return MarksEntryResponse.from(e);
    }

    private MarksEntry find(Long id) {
        return marksRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("MarksEntry", id));
    }
}
