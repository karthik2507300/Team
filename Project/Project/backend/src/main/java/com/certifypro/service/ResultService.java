package com.certifypro.service;

import com.certifypro.dto.response.CandidateResultResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.*;
import com.certifypro.model.enums.*;
import com.certifypro.repository.*;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.AuditLogUtil;
import com.certifypro.util.CertificateNumberGenerator;
import com.certifypro.util.PageUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class ResultService {

    private static final String MODULE = "CandidateResult";
    private static final String CERT_MODULE = "Certificate";

    private final CandidateResultRepository resultRepository;
    private final ExamWindowRepository examWindowRepository;
    private final GradingScaleRepository gradingScaleRepository;
    private final SeatAllocationRepository seatRepository;
    private final ScriptAllocationRepository scriptRepository;
    private final MarksEntryRepository marksRepository;
    private final QuestionPaperRepository paperRepository;
    private final CertificationProgramRepository programRepository;
    private final CertificateRepository certificateRepository;
    private final CandidateRepository candidateRepository;
    private final CertificateNumberGenerator certNumberGenerator;
    private final NotificationService notificationService;
    private final AuditLogUtil auditLog;

    public ResultService(CandidateResultRepository resultRepository,
                         ExamWindowRepository examWindowRepository,
                         GradingScaleRepository gradingScaleRepository,
                         SeatAllocationRepository seatRepository,
                         ScriptAllocationRepository scriptRepository,
                         MarksEntryRepository marksRepository,
                         QuestionPaperRepository paperRepository,
                         CertificationProgramRepository programRepository,
                         CertificateRepository certificateRepository,
                         CandidateRepository candidateRepository,
                         CertificateNumberGenerator certNumberGenerator,
                         NotificationService notificationService,
                         AuditLogUtil auditLog) {
        this.resultRepository = resultRepository;
        this.examWindowRepository = examWindowRepository;
        this.gradingScaleRepository = gradingScaleRepository;
        this.seatRepository = seatRepository;
        this.scriptRepository = scriptRepository;
        this.marksRepository = marksRepository;
        this.paperRepository = paperRepository;
        this.programRepository = programRepository;
        this.certificateRepository = certificateRepository;
        this.candidateRepository = candidateRepository;
        this.certNumberGenerator = certNumberGenerator;
        this.notificationService = notificationService;
        this.auditLog = auditLog;
    }

    /** Rule 4: compute draft results for every candidate seated in the window. */
    @Transactional
    public List<CandidateResultResponse> compute(Long windowId) {
        ExamWindow window = examWindowRepository.findById(windowId)
                .orElseThrow(() -> NotFoundException.of("ExamWindow", windowId));
        Long programId = window.getProgramId();

        List<GradingScale> scale = gradingScaleRepository.findByProgramId(programId);
        if (scale.isEmpty()) {
            throw new BusinessException("No grading scale configured for the program");
        }

        List<CandidateResult> results = seatRepository.findByWindowId(windowId).stream().map(seat -> {
            int totalMarks = 0;
            int obtained = 0;
            boolean hasMarks = false;

            for (ScriptAllocation script : scriptRepository.findByAllocationId(seat.getAllocationId())) {
                QuestionPaper paper = paperRepository.findById(script.getPaperId()).orElse(null);
                int paperTotal = (paper == null || paper.getTotalMarks() == null) ? 0 : paper.getTotalMarks();
                List<MarksEntry> entries = marksRepository.findByScriptId(script.getScriptId());
                if (!entries.isEmpty()) {
                    int avg = (int) Math.round(entries.stream()
                            .mapToInt(MarksEntry::getMarksAwarded).average().orElse(0));
                    obtained += avg;
                    totalMarks += paperTotal;
                    hasMarks = true;
                }
            }

            CandidateResult r = resultRepository
                    .findByCandidateIdAndWindowIdAndProgramId(seat.getCandidateId(), windowId, programId)
                    .orElseGet(CandidateResult::new);
            if (r.getStatus() == ResultStatus.Published) {
                return r; // don't recompute published results
            }

            r.setCandidateId(seat.getCandidateId());
            r.setWindowId(windowId);
            r.setProgramId(programId);
            r.setTotalMarks(totalMarks);
            r.setMarksObtained(obtained);

            if (!hasMarks) {
                r.setPercentage(0f);
                r.setGrade(null);
                r.setOutcome(ResultOutcome.Absent);
            } else {
                float pct = totalMarks == 0 ? 0f : (obtained * 100f / totalMarks);
                r.setPercentage(pct);
                applyGrade(r, scale, pct);
            }
            r.setStatus(ResultStatus.Draft);
            CandidateResult saved = resultRepository.save(r);
            auditLog.log("COMPUTE", MODULE, saved.getResultId());
            return saved;
        }).toList();

        return results.stream().map(CandidateResultResponse::from).toList();
    }

    private void applyGrade(CandidateResult r, List<GradingScale> scale, float pct) {
        GradingScale band = scale.stream()
                .filter(b -> pct >= b.getMinPercentage() && pct <= b.getMaxPercentage())
                .findFirst().orElse(null);
        if (band == null) {
            r.setGrade(null);
            r.setOutcome(ResultOutcome.Fail);
        } else {
            r.setGrade(band.getGradeLetter());
            r.setOutcome(Boolean.TRUE.equals(band.getIsPassing()) ? ResultOutcome.Pass : ResultOutcome.Fail);
        }
    }

    /** Rule 5: publishing a Pass result auto-issues a certificate. */
    @Transactional
    public CandidateResultResponse publish(Long resultId) {
        CandidateResult r = resultRepository.findById(resultId)
                .orElseThrow(() -> NotFoundException.of("CandidateResult", resultId));

        r.setStatus(ResultStatus.Published);
        r.setPublishedDate(LocalDate.now());
        resultRepository.save(r);
        auditLog.log("PUBLISH", MODULE, r.getResultId());

        if (r.getOutcome() == ResultOutcome.Pass) {
            issueCertificate(r);
        }

        candidateRepository.findById(r.getCandidateId())
                .map(Candidate::getUserId)
                .ifPresent(uid -> notificationService.notifyUser(uid, "Result",
                        "Your result for window #" + r.getWindowId() + " has been published"));

        return CandidateResultResponse.from(r);
    }

    private void issueCertificate(CandidateResult r) {
        CertificationProgram program = programRepository.findById(r.getProgramId()).orElse(null);
        LocalDate issued = LocalDate.now();
        LocalDate validUntil = (program != null && program.getValidityYears() != null)
                ? issued.plusYears(program.getValidityYears()) : null;

        Certificate cert = new Certificate();
        cert.setCandidateId(r.getCandidateId());
        cert.setProgramId(r.getProgramId());
        cert.setCertificateNumber(certNumberGenerator.generate());
        cert.setIssuedDate(issued);
        cert.setValidUntil(validUntil);
        cert.setIssuedById(SecurityUtil.currentUserId());
        cert.setStatus(CertificateStatus.Valid);
        cert = certificateRepository.save(cert);
        auditLog.log("AUTO_ISSUE", CERT_MODULE, cert.getCertificateId());
    }

    public PageResponse<CandidateResultResponse> view(Long candidateId, Long windowId, int page, int limit) {
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            Candidate own = candidateRepository.findByUserId(SecurityUtil.currentUserId()).orElse(null);
            if (own == null) {
                throw new AccessDeniedException("No candidate profile for this account");
            }
            if (candidateId != null && !Objects.equals(candidateId, own.getCandidateId())) {
                throw new AccessDeniedException("You can only view your own results");
            }
            candidateId = own.getCandidateId();
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
