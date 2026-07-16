package com.certifypro.service;

import com.certifypro.dto.request.CreateQuestionRequest;
import com.certifypro.dto.request.UpdateQuestionRequest;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.QuestionResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.Question;
import com.certifypro.model.enums.Difficulty;
import com.certifypro.model.enums.QuestionStatus;
import com.certifypro.model.enums.QuestionType;
import com.certifypro.repository.CertificationProgramRepository;
import com.certifypro.repository.QuestionRepository;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final CertificationProgramRepository programRepository;

    public QuestionService(QuestionRepository questionRepository,
                           CertificationProgramRepository programRepository) {
        this.questionRepository = questionRepository;
        this.programRepository = programRepository;
    }

    @Transactional
    public QuestionResponse create(CreateQuestionRequest req) {
        if (!programRepository.existsById(req.programId())) {
            throw NotFoundException.of("CertificationProgram", req.programId());
        }
        Question q = new Question();
        q.setProgramId(req.programId());
        q.setTopicTag(req.topicTag());
        q.setDifficulty(parseDifficulty(req.difficulty()));
        q.setQuestionText(req.questionText());
        q.setType(parseType(req.type()));
        q.setMarks(req.marks() == null ? 0 : req.marks());
        q.setCreatedById(SecurityUtil.currentUserId());
        q.setStatus(QuestionStatus.Active);
        return QuestionResponse.from(questionRepository.save(q));
    }

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
