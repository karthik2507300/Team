package com.certifypro.result.service.impl;

import com.certifypro.result.client.CandidateServiceGateway;
import com.certifypro.result.client.CertificateServiceGateway;
import com.certifypro.result.client.ExamServiceGateway;
import com.certifypro.result.client.NotificationServiceGateway;
import com.certifypro.result.client.QuestionServiceGateway;
import com.certifypro.result.client.dto.CandidateDto;
import com.certifypro.result.client.dto.ExamWindowDto;
import com.certifypro.result.client.dto.GradingScaleDto;
import com.certifypro.result.client.dto.PaperDto;
import com.certifypro.result.client.dto.SeatAllocationDto;
import com.certifypro.result.common.ResultOutcome;
import com.certifypro.result.common.ResultStatus;
import com.certifypro.result.dto.response.CandidateResultResponse;
import com.certifypro.result.dto.response.PageResponse;
import com.certifypro.result.entity.CandidateResult;
import com.certifypro.result.entity.MarksEntry;
import com.certifypro.result.entity.ScriptAllocation;
import com.certifypro.result.exception.NotFoundException;
import com.certifypro.result.repository.CandidateResultRepository;
import com.certifypro.result.repository.MarksEntryRepository;
import com.certifypro.result.repository.ScriptAllocationRepository;
import com.certifypro.result.security.SecurityUtil;
import com.certifypro.result.service.ResultService;
import com.certifypro.result.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ResultServiceImpl implements ResultService {

    private static final Logger log = LoggerFactory.getLogger(ResultServiceImpl.class);

    private final CandidateResultRepository resultRepository;
    private final ScriptAllocationRepository scriptRepository;
    private final MarksEntryRepository marksRepository;
    private final QuestionServiceGateway questionGateway;
    private final ExamServiceGateway examGateway;
    private final CandidateServiceGateway candidateGateway;
    private final CertificateServiceGateway certificateGateway;
    private final NotificationServiceGateway notificationGateway;

    public ResultServiceImpl(CandidateResultRepository resultRepository,
                             ScriptAllocationRepository scriptRepository,
                             MarksEntryRepository marksRepository,
                             QuestionServiceGateway questionGateway,
                             ExamServiceGateway examGateway,
                             CandidateServiceGateway candidateGateway,
                             CertificateServiceGateway certificateGateway,
                             NotificationServiceGateway notificationGateway) {
        this.resultRepository = resultRepository;
        this.scriptRepository = scriptRepository;
        this.marksRepository = marksRepository;
        this.questionGateway = questionGateway;
        this.examGateway = examGateway;
        this.candidateGateway = candidateGateway;
        this.certificateGateway = certificateGateway;
        this.notificationGateway = notificationGateway;
    }

    /**
     * Rule 4: compute draft results for every candidate with marks in the window.
     *
     * <p><b>SIMPLIFICATION vs. monolith.</b> The monolith derived the candidate set
     * from SeatAllocation rows for the window (seat data now lives in exam-service)
     * and read the grading scale + paper totals from local repositories. This
     * microservice sources results from LOCAL data instead:
     * <ol>
     *   <li>The candidate set is derived from the LOCAL script-allocations + marks-entries
     *       for the window (grouped by candidate), NOT re-derived from seat allocations.
     *       A script-allocation is considered "in the window" when its paper (looked up
     *       via question-service) reports the same windowId. Only candidates who actually
     *       have marks are produced (candidates with zero marks are simply not in the
     *       local marks data, so no Absent row is created — this differs from the monolith
     *       which produced an Absent row for every seated candidate).</li>
     *   <li>The programId is resolved from exam-service's exam-window lookup, falling back
     *       to the programId carried on the papers if exam-service is unavailable.</li>
     *   <li>The grading scale comes from candidate-service; if it is empty/unavailable a
     *       DEFAULT scale is applied (&gt;=80 A, &gt;=65 B, &gt;=50 C pass, else F fail),
     *       whereas the monolith threw an error when no scale was configured.</li>
     *   <li>Paper total marks come from question-service (was a local paper repo).</li>
     *   <li>Audit logging is dropped (captured by the LoggingAspect instead).</li>
     * </ol>
     * The grading math itself (per-script average of dual marks, sum obtained / sum of
     * paper totals, percentage, grade band lookup, Pass/Fail/Absent outcome, and the
     * "never recompute a Published result" guard) is ported faithfully.
     */
    @Override
    @Transactional
    public List<CandidateResultResponse> compute(Long windowId) {
        // 1. Resolve the program for the window (exam-service; fallback derives from papers).
        ExamWindowDto window = examGateway.getExamWindow(windowId);
        Long programId = window == null ? null : window.programId();

        // 2. Gather local script-allocations that belong to this window (paper.windowId == windowId).
        //    Cache paper lookups to avoid repeated Feign calls per script.
        Map<Long, PaperDto> paperCache = new LinkedHashMap<>();
        List<ScriptAllocation> windowScripts = new ArrayList<>();
        for (ScriptAllocation script : scriptRepository.findAll()) {
            PaperDto paper = paperCache.computeIfAbsent(script.getPaperId(), questionGateway::getPaper);
            if (paper != null && Objects.equals(paper.windowId(), windowId)) {
                windowScripts.add(script);
                if (programId == null && paper.programId() != null) {
                    programId = paper.programId();
                }
            }
        }

        if (programId == null) {
            throw new NotFoundException("Unable to resolve programId for window " + windowId
                    + " (exam-service and paper metadata both unavailable)");
        }

        // 3. Grading scale (candidate-service; default scale when empty/unavailable).
        List<GradingScaleDto> scale = candidateGateway.getGradingScale(programId);
        if (scale.isEmpty()) {
            log.warn("No grading scale for programId={}. Applying default scale.", programId);
            scale = defaultScale();
        }

        // 4. Group obtained/total marks by candidate (candidate resolved via exam-service seat lookup).
        Map<Long, int[]> byCandidate = new LinkedHashMap<>(); // candidateId -> [obtained, total]
        for (ScriptAllocation script : windowScripts) {
            SeatAllocationDto seat = examGateway.getSeatAllocation(script.getAllocationId());
            if (seat == null || seat.candidateId() == null) {
                log.warn("Skipping scriptId={}: could not resolve candidate for allocationId={}.",
                        script.getScriptId(), script.getAllocationId());
                continue;
            }
            List<MarksEntry> entries = marksRepository.findByScript_ScriptId(script.getScriptId());
            if (entries.isEmpty()) {
                continue;
            }
            PaperDto paper = paperCache.get(script.getPaperId());
            int paperTotal = (paper == null || paper.totalMarks() == null) ? 0 : paper.totalMarks();
            int avg = (int) Math.round(entries.stream()
                    .mapToInt(MarksEntry::getMarksAwarded).average().orElse(0));

            int[] agg = byCandidate.computeIfAbsent(seat.candidateId(), k -> new int[2]);
            agg[0] += avg;
            agg[1] += paperTotal;
        }

        // 5. Upsert a Draft result per candidate (skip Published), grade + set outcome.
        List<CandidateResult> results = new ArrayList<>();
        for (Map.Entry<Long, int[]> e : byCandidate.entrySet()) {
            Long candidateId = e.getKey();
            int obtained = e.getValue()[0];
            int totalMarks = e.getValue()[1];

            CandidateResult r = resultRepository
                    .findByCandidateIdAndWindowIdAndProgramId(candidateId, windowId, programId)
                    .orElseGet(CandidateResult::new);
            if (r.getStatus() == ResultStatus.Published) {
                results.add(r); // don't recompute published results
                continue;
            }

            r.setCandidateId(candidateId);
            r.setWindowId(windowId);
            r.setProgramId(programId);
            r.setTotalMarks(totalMarks);
            r.setMarksObtained(obtained);

            float pct = totalMarks == 0 ? 0f : (obtained * 100f / totalMarks);
            r.setPercentage(pct);
            applyGrade(r, scale, pct);

            r.setStatus(ResultStatus.Draft);
            results.add(resultRepository.save(r));
        }

        return results.stream().map(CandidateResultResponse::from).toList();
    }

    private void applyGrade(CandidateResult r, List<GradingScaleDto> scale, float pct) {
        GradingScaleDto band = scale.stream()
                .filter(b -> b.minPercentage() != null && b.maxPercentage() != null
                        && pct >= b.minPercentage() && pct <= b.maxPercentage())
                .findFirst().orElse(null);
        if (band == null) {
            r.setGrade(null);
            r.setOutcome(ResultOutcome.Fail);
        } else {
            r.setGrade(band.gradeLetter());
            r.setOutcome(Boolean.TRUE.equals(band.isPassing()) ? ResultOutcome.Pass : ResultOutcome.Fail);
        }
    }

    /** Default grading scale used when candidate-service has none: &gt;=50% passes. */
    private List<GradingScaleDto> defaultScale() {
        return List.of(
                new GradingScaleDto("A", 80f, 100f, true),
                new GradingScaleDto("B", 65f, 79.99f, true),
                new GradingScaleDto("C", 50f, 64.99f, true),
                new GradingScaleDto("F", 0f, 49.99f, false)
        );
    }

    /**
     * Rule 5: publishing a Pass result auto-issues a certificate.
     *
     * <p><b>SIMPLIFICATION vs. monolith.</b> The monolith created the Certificate row
     * locally (via CertificateNumberGenerator + CertificateRepository and looked up the
     * program's validityYears itself). Here the Certificate aggregate is owned by
     * certificate-service, so publish() delegates to it via the certificate-service
     * circuit-breaker gateway (POST /api/certificates/internal/issue). Certificate
     * numbering + validity are certificate-service's responsibility. If certificate-service
     * is down the publish still succeeds (issuance is logged and can be retried later).
     * The candidate's owning userId is resolved via candidate-service (was a local
     * Candidate lookup) to send the "result published" notification. Audit logging is dropped.
     */
    @Override
    @Transactional
    public CandidateResultResponse publish(Long resultId) {
        CandidateResult r = resultRepository.findById(resultId)
                .orElseThrow(() -> NotFoundException.of("CandidateResult", resultId));

        r.setStatus(ResultStatus.Published);
        r.setPublishedDate(LocalDate.now());
        resultRepository.save(r);

        if (r.getOutcome() == ResultOutcome.Pass) {
            certificateGateway.issue(r.getCandidateId(), r.getProgramId());
        }

        CandidateDto candidate = candidateGateway.getCandidate(r.getCandidateId());
        if (candidate != null && candidate.userId() != null) {
            notificationGateway.notifyUser(candidate.userId(), "Result",
                    "Your result for window #" + r.getWindowId() + " has been published");
        }

        return CandidateResultResponse.from(r);
    }

    @Override
    public PageResponse<CandidateResultResponse> view(Long candidateId, Long windowId, int page, int limit) {
        // SIMPLIFICATION vs. monolith: the monolith looked up the caller's Candidate row by
        // userId (local repo) to force a Candidate to only see their own results. candidate-service
        // exposes lookup by candidateId only (no by-userId endpoint), so a Candidate role must
        // supply their own candidateId as a query param; the service cannot silently derive it.
        // A Candidate request without a candidateId is rejected rather than leaking all results.
        if ("Candidate".equals(SecurityUtil.currentRole()) && candidateId == null) {
            throw new AccessDeniedException(
                    "Candidates must supply their candidateId to view results");
        }

        if (candidateId != null && windowId != null) {
            return PageResponse.from(resultRepository
                    .findByCandidateIdAndWindowId(candidateId, windowId, PageUtil.of(page, limit))
                    .map(CandidateResultResponse::from));
        } else if (candidateId != null) {
            return PageResponse.from(resultRepository
                    .findByCandidateId(candidateId, PageUtil.of(page, limit))
                    .map(CandidateResultResponse::from));
        } else if (windowId != null) {
            return PageResponse.from(resultRepository
                    .findByWindowId(windowId, PageUtil.of(page, limit))
                    .map(CandidateResultResponse::from));
        }
        return PageResponse.from(resultRepository.findAll(PageUtil.of(page, limit))
                .map(CandidateResultResponse::from));
    }
}
