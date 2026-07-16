package com.certifypro.result.service;

import com.certifypro.result.dto.response.CandidateResultResponse;
import com.certifypro.result.dto.response.PageResponse;

import java.util.List;

public interface ResultService {

    List<CandidateResultResponse> compute(Long windowId);

    CandidateResultResponse publish(Long resultId);

    PageResponse<CandidateResultResponse> view(Long candidateId, Long windowId, int page, int limit);
}
