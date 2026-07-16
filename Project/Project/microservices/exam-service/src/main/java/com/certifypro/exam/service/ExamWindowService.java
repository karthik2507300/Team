package com.certifypro.exam.service;

import com.certifypro.exam.dto.request.CreateExamWindowRequest;
import com.certifypro.exam.dto.request.UpdateExamWindowRequest;
import com.certifypro.exam.dto.response.ExamWindowResponse;
import com.certifypro.exam.dto.response.PageResponse;

public interface ExamWindowService {

    ExamWindowResponse create(CreateExamWindowRequest req);

    PageResponse<ExamWindowResponse> list(Long programId, int page, int limit);

    ExamWindowResponse getById(Long id);

    ExamWindowResponse update(Long id, UpdateExamWindowRequest req);
}
