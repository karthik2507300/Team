package com.certifypro.service;

import com.certifypro.dto.request.CreateReEvaluationRequest;
import com.certifypro.dto.response.ReEvaluationResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Candidate;
import com.certifypro.model.CandidateResult;
import com.certifypro.model.ReEvaluationRequest;
import com.certifypro.model.enums.ReEvalStatus;
import com.certifypro.model.enums.ResultStatus;
import com.certifypro.repository.CandidateRepository;
import com.certifypro.repository.CandidateResultRepository;
import com.certifypro.repository.ReEvaluationRequestRepository;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.AuditLogUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class ReEvaluationService {

    private static final String MODULE = "ReEvaluationRequest";

    private final ReEvaluationRequestRepository requestRepository;
    private final CandidateResultRepository resultRepository;
    private final CandidateRepository candidateRepository;
    private final AuditLogUtil auditLog;

    public ReEvaluationService(ReEvaluationRequestRepository requestRepository,
                               CandidateResultRepository resultRepository,
                               CandidateRepository candidateRepository,
                               AuditLogUtil auditLog) {
        this.requestRepository = requestRepository;
        this.resultRepository = resultRepository;
        this.candidateRepository = candidateRepository;
        this.auditLog = auditLog;
    }

    @Transactional
    public ReEvaluationResponse submit(CreateReEvaluationRequest req) {
        Candidate candidate = candidateRepository.findByUserId(SecurityUtil.currentUserId())
                .orElseThrow(() -> new AccessDeniedException("No candidate profile for this account"));
        CandidateResult result = resultRepository.findById(req.resultId())
                .orElseThrow(() -> NotFoundException.of("CandidateResult", req.resultId()));
        if (!Objects.equals(result.getCandidateId(), candidate.getCandidateId())) {
            throw new AccessDeniedException("You can only request re-evaluation of your own results");
        }

        ReEvaluationRequest r = new ReEvaluationRequest();
        r.setResultId(req.resultId());
        r.setCandidateId(candidate.getCandidateId());
        r.setRequestDate(LocalDate.now());
        r.setReason(req.reason());
        r.setStatus(ReEvalStatus.Submitted);
        r = requestRepository.save(r);
        auditLog.log("CREATE", MODULE, r.getRequestId());

        result.setStatus(ResultStatus.UnderReEvaluation);
        resultRepository.save(result);
        auditLog.log("UNDER_RE_EVALUATION", "CandidateResult", result.getResultId());

        return ReEvaluationResponse.from(r);
    }

    @Transactional
    public ReEvaluationResponse resolve(Long id) {
        ReEvaluationRequest r = requestRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("ReEvaluationRequest", id));
        r.setStatus(ReEvalStatus.Resolved);
        r = requestRepository.save(r);
        auditLog.log("RESOLVE", MODULE, r.getRequestId());

        resultRepository.findById(r.getResultId()).ifPresent(result -> {
            result.setStatus(ResultStatus.Revised);
            resultRepository.save(result);
            auditLog.log("REVISED", "CandidateResult", result.getResultId());
        });

        return ReEvaluationResponse.from(r);
    }
}
