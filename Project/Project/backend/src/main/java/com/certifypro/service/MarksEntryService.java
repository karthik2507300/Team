package com.certifypro.service;

import com.certifypro.dto.request.CreateMarksEntryRequest;
import com.certifypro.dto.response.MarksEntryResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.MarksEntry;
import com.certifypro.model.QuestionPaper;
import com.certifypro.model.ScriptAllocation;
import com.certifypro.model.enums.MarksStatus;
import com.certifypro.model.enums.ScriptStatus;
import com.certifypro.repository.MarksEntryRepository;
import com.certifypro.repository.QuestionPaperRepository;
import com.certifypro.repository.ScriptAllocationRepository;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.AuditLogUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MarksEntryService {

    private static final String MODULE = "MarksEntry";

    private final MarksEntryRepository marksRepository;
    private final ScriptAllocationRepository scriptRepository;
    private final QuestionPaperRepository paperRepository;
    private final NotificationService notificationService;
    private final AuditLogUtil auditLog;

    public MarksEntryService(MarksEntryRepository marksRepository,
                             ScriptAllocationRepository scriptRepository,
                             QuestionPaperRepository paperRepository,
                             NotificationService notificationService,
                             AuditLogUtil auditLog) {
        this.marksRepository = marksRepository;
        this.scriptRepository = scriptRepository;
        this.paperRepository = paperRepository;
        this.notificationService = notificationService;
        this.auditLog = auditLog;
    }

    /** Dual marking: max two entries per script; flag for moderation when they differ &gt; 10% of total. */
    @Transactional
    public MarksEntryResponse submit(CreateMarksEntryRequest req) {
        ScriptAllocation script = scriptRepository.findById(req.scriptId())
                .orElseThrow(() -> NotFoundException.of("ScriptAllocation", req.scriptId()));

        long existing = marksRepository.countByScriptId(req.scriptId());
        if (existing >= 2) {
            throw new BusinessException("This script already has two marks entries (dual marking complete)");
        }

        MarksEntry entry = new MarksEntry();
        entry.setScriptId(req.scriptId());
        entry.setEvaluatorId(SecurityUtil.currentUserId());
        entry.setMarksAwarded(req.marksAwarded());
        entry.setEntryDate(LocalDate.now());
        entry.setStatus(MarksStatus.Submitted);
        entry = marksRepository.save(entry);

        auditLog.log("CREATE", MODULE, entry.getMarksId());

        if (existing + 1 == 2) {
            evaluateDualMarking(script);
        } else {
            script.setStatus(ScriptStatus.UnderEvaluation);
            scriptRepository.save(script);
        }

        return MarksEntryResponse.from(entry);
    }

    private void evaluateDualMarking(ScriptAllocation script) {
        List<MarksEntry> entries = marksRepository.findByScriptId(script.getScriptId());
        QuestionPaper paper = paperRepository.findById(script.getPaperId()).orElse(null);
        int total = (paper == null || paper.getTotalMarks() == null) ? 0 : paper.getTotalMarks();

        int diff = Math.abs(entries.get(0).getMarksAwarded() - entries.get(1).getMarksAwarded());
        double threshold = 0.10 * total;

        if (total > 0 && diff > threshold) {
            // Flag both entries for moderation and notify the Exam Controller.
            for (MarksEntry e : entries) {
                e.setStatus(MarksStatus.Moderated);
                marksRepository.save(e);
            }
            notificationService.notifyRole("ExamController", "Result",
                    "Script #" + script.getScriptId() + " flagged for moderation: dual-mark difference "
                            + diff + " exceeds 10% of " + total);
        }

        script.setStatus(ScriptStatus.MarksSubmitted);
        scriptRepository.save(script);
    }

    public com.certifypro.dto.response.PageResponse<MarksEntryResponse> list(
            String status, Long scriptId, int page, int limit) {
        if (scriptId != null) {
            java.util.List<MarksEntryResponse> list = marksRepository.findByScriptId(scriptId)
                    .stream().map(MarksEntryResponse::from).toList();
            return new com.certifypro.dto.response.PageResponse<>(list, 0, list.size(), list.size(), 1, true);
        }
        MarksStatus st = status == null ? MarksStatus.Moderated : MarksStatus.valueOf(status);
        return com.certifypro.dto.response.PageResponse.from(
                marksRepository.findByStatus(st, com.certifypro.util.PageUtil.of(page, limit))
                        .map(MarksEntryResponse::from));
    }

    @Transactional
    public MarksEntryResponse verify(Long id) {
        MarksEntry e = find(id);
        e.setStatus(MarksStatus.Verified);
        e.setVerifiedById(SecurityUtil.currentUserId());
        e = marksRepository.save(e);
        auditLog.log("VERIFY", MODULE, e.getMarksId());
        return MarksEntryResponse.from(e);
    }

    @Transactional
    public MarksEntryResponse moderate(Long id) {
        MarksEntry e = find(id);
        e.setStatus(MarksStatus.Moderated);
        e.setVerifiedById(SecurityUtil.currentUserId());
        e = marksRepository.save(e);
        auditLog.log("MODERATE", MODULE, e.getMarksId());
        return MarksEntryResponse.from(e);
    }

    private MarksEntry find(Long id) {
        return marksRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("MarksEntry", id));
    }
}
