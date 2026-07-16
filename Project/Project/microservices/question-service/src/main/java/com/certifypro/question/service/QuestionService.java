package com.certifypro.question.service;

import com.certifypro.question.dto.request.CreateQuestionRequest;
import com.certifypro.question.dto.request.UpdateQuestionRequest;
import com.certifypro.question.dto.response.PageResponse;
import com.certifypro.question.dto.response.QuestionResponse;

public interface QuestionService {

    QuestionResponse create(CreateQuestionRequest req);

    PageResponse<QuestionResponse> filter(Long programId, String difficulty, String type, int page, int limit);

    QuestionResponse update(Long id, UpdateQuestionRequest req);
}
