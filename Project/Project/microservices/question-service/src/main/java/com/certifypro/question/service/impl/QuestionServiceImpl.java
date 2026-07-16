package com.certifypro.question.service.impl;

import com.certifypro.question.common.Difficulty;
import com.certifypro.question.common.QuestionStatus;
import com.certifypro.question.common.QuestionType;
import com.certifypro.question.dto.request.CreateQuestionRequest;
import com.certifypro.question.dto.request.UpdateQuestionRequest;
import com.certifypro.question.dto.response.PageResponse;
import com.certifypro.question.dto.response.QuestionResponse;
import com.certifypro.question.entity.Question;
import com.certifypro.question.exception.NotFoundException;
import com.certifypro.question.repository.QuestionRepository;
import com.certifypro.question.security.SecurityUtil;
import com.certifypro.question.service.QuestionService;
import com.certifypro.question.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public QuestionResponse create(CreateQuestionRequest req) {
        // programId is a cross-service reference (candidate-service). Per spec rule 2,
        // pure existence-only FK validation is dropped — accept the id as-is.
        Question q = Question.builder()
                .programId(req.programId())
                .topicTag(req.topicTag())
                .difficulty(parseDifficulty(req.difficulty()))
                .questionText(req.questionText())
                .type(parseType(req.type()))
                .marks(req.marks() == null ? 0 : req.marks())
                .createdById(SecurityUtil.currentUserId())
                .status(QuestionStatus.Active)
                .build();
        return QuestionResponse.from(questionRepository.save(q));
    }

    @Override
    public PageResponse<QuestionResponse> filter(Long programId, String difficulty,
                                                 String type, int page, int limit) {
        Pageable pageable = PageUtil.of(page, limit);
        Difficulty diff = difficulty == null ? null : parseDifficulty(difficulty);
        QuestionType qType = type == null ? null : parseType(type);

        Page<Question> result;
        if (programId == null) {
            result = questionRepository.findAll(pageable);
        } else if (diff != null && qType != null) {
            result = questionRepository.findByProgramIdAndDifficultyAndType(programId, diff, qType, pageable);
        } else if (diff != null) {
            result = questionRepository.findByProgramIdAndDifficulty(programId, diff, pageable);
        } else if (qType != null) {
            result = questionRepository.findByProgramIdAndType(programId, qType, pageable);
        } else {
            result = questionRepository.findByProgramId(programId, pageable);
        }
        return PageResponse.from(result.map(QuestionResponse::from));
    }

    @Override
    @Transactional
    public QuestionResponse update(Long id, UpdateQuestionRequest req) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("Question", id));
        if (req.topicTag() != null) q.setTopicTag(req.topicTag());
        if (req.difficulty() != null) q.setDifficulty(parseDifficulty(req.difficulty()));
        if (req.questionText() != null) q.setQuestionText(req.questionText());
        if (req.type() != null) q.setType(parseType(req.type()));
        if (req.marks() != null) q.setMarks(req.marks());
        if (req.status() != null) q.setStatus(parseStatus(req.status()));
        return QuestionResponse.from(questionRepository.save(q));
    }

    private Difficulty parseDifficulty(String v) {
        try {
            return Difficulty.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid difficulty: " + v + " (Easy, Medium, Hard)");
        }
    }

    private QuestionType parseType(String v) {
        try {
            return QuestionType.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid type: " + v + " (MCQ, Descriptive, CaseStudy, Practical)");
        }
    }

    private QuestionStatus parseStatus(String v) {
        try {
            return QuestionStatus.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + v + " (Active, Retired, UnderReview)");
        }
    }
}
