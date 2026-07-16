package com.certifypro.result.service;

import com.certifypro.result.dto.request.CreateReEvaluationRequest;
import com.certifypro.result.dto.response.ReEvaluationResponse;

public interface ReEvaluationService {

    ReEvaluationResponse submit(CreateReEvaluationRequest req);

    ReEvaluationResponse resolve(Long id);
}
