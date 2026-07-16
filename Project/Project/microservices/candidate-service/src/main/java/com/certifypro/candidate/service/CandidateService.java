package com.certifypro.candidate.service;

import com.certifypro.candidate.dto.internal.RegisterCandidateRequest;
import com.certifypro.candidate.dto.request.CreateCandidateRequest;
import com.certifypro.candidate.dto.request.UpdateCandidateRequest;
import com.certifypro.candidate.dto.response.CandidateResponse;

/** Candidate profile use cases. */
public interface CandidateService {

    CandidateResponse create(CreateCandidateRequest req);

    CandidateResponse getById(Long id);

    /** The candidate profile of the currently authenticated user. */
    CandidateResponse getMine();

    CandidateResponse update(Long id, UpdateCandidateRequest req);

    /** Service-to-service: create a candidate profile at registration (status Active). */
    CandidateResponse register(RegisterCandidateRequest req);
}
