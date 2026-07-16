package com.certifypro.candidate.service;

import com.certifypro.candidate.dto.request.CreateEnrolmentRequest;
import com.certifypro.candidate.dto.request.UpdateEligibilityRequest;
import com.certifypro.candidate.dto.response.EnrolmentResponse;
import com.certifypro.candidate.dto.response.PageResponse;

/** Program enrolment use cases. */
public interface EnrolmentService {

    EnrolmentResponse create(CreateEnrolmentRequest req);

    PageResponse<EnrolmentResponse> list(String eligibilityStatus, int page, int limit);

    EnrolmentResponse getById(Long id);

    EnrolmentResponse updateEligibility(Long id, UpdateEligibilityRequest req);
}
