package com.certifypro.result.service.impl;

import com.certifypro.result.common.ReEvalStatus;
import com.certifypro.result.common.ResultStatus;
import com.certifypro.result.dto.request.CreateReEvaluationRequest;
import com.certifypro.result.dto.response.ReEvaluationResponse;
import com.certifypro.result.entity.CandidateResult;
import com.certifypro.result.entity.ReEvaluationRequest;
import com.certifypro.result.exception.NotFoundException;
import com.certifypro.result.repository.CandidateResultRepository;
import com.certifypro.result.repository.ReEvaluationRequestRepository;
import com.certifypro.result.service.ReEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ReEvaluationServiceImpl implements ReEvaluationService {

    private final ReEvaluationRequestRepository requestRepository;
    private final CandidateResultRepository resultRepository;

    public ReEvaluationServiceImpl(ReEvaluationRequestRepository requestRepository,
                                   CandidateResultRepository resultRepository) {
        this.requestRepository = requestRepository;
        this.resultRepository = resultRepository;
    }

    /**
     * Submit a re-evaluation request for a published result.
     *
     * <p>SIMPLIFICATION vs. monolith: the monolith resolved the caller's Candidate row by
     * userId (local repo) and asserted {@code result.candidateId == caller.candidateId}.
     * candidate-service exposes no lookup-by-userId endpoint, so that per-user ownership
     * assertion is dropped; the request's candidateId is taken from the target result. The
     * @PreAuthorize("hasRole('Candidate')") on the controller still restricts this to
     * candidates. Audit logging is dropped (captured by the LoggingAspect). The intra-service
     * @ManyToOne relationship to CandidateResult replaces the raw resultId field.
     */
    @Override
    @Transactional
    public ReEvaluationResponse submit(CreateReEvaluationRequest req) {
        CandidateResult result = resultRepository.findById(req.resultId())
                .orElseThrow(() -> NotFoundException.of("CandidateResult", req.resultId()));

        ReEvaluationRequest r = ReEvaluationRequest.builder()
                .result(result)
                .candidateId(result.getCandidateId())
                .requestDate(LocalDate.now())
                .reason(req.reason())
                .status(ReEvalStatus.Submitted)
                .build();
        r = requestRepository.save(r);

        result.setStatus(ResultStatus.UnderReEvaluation);
        resultRepository.save(result);

        return ReEvaluationResponse.from(r);
    }

    @Override
    @Transactional
    public ReEvaluationResponse resolve(Long id) {
        ReEvaluationRequest r = requestRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("ReEvaluationRequest", id));
        r.setStatus(ReEvalStatus.Resolved);
        r = requestRepository.save(r);

        CandidateResult result = r.getResult();
        if (result != null) {
            result.setStatus(ResultStatus.Revised);
            resultRepository.save(result);
        }

        return ReEvaluationResponse.from(r);
    }
}
