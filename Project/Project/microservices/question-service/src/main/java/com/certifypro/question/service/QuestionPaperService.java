package com.certifypro.question.service;

import com.certifypro.question.dto.request.AddPaperQuestionsRequest;
import com.certifypro.question.dto.request.CreateQuestionPaperRequest;
import com.certifypro.question.dto.request.UpdatePaperStatusRequest;
import com.certifypro.question.dto.response.PaperDto;
import com.certifypro.question.dto.response.QuestionPaperResponse;

public interface QuestionPaperService {

    QuestionPaperResponse create(CreateQuestionPaperRequest req);

    QuestionPaperResponse getById(Long id);

    QuestionPaperResponse addQuestions(Long paperId, AddPaperQuestionsRequest req);

    QuestionPaperResponse updateStatus(Long paperId, UpdatePaperStatusRequest req);

    /** Raw internal projection for service-to-service calls (result-service). */
    PaperDto getInternal(Long paperId);
}
