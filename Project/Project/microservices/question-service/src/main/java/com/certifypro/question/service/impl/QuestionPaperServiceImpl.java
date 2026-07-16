package com.certifypro.question.service.impl;

import com.certifypro.question.common.PaperStatus;
import com.certifypro.question.dto.request.AddPaperQuestionsRequest;
import com.certifypro.question.dto.request.CreateQuestionPaperRequest;
import com.certifypro.question.dto.request.UpdatePaperStatusRequest;
import com.certifypro.question.dto.response.PaperDto;
import com.certifypro.question.dto.response.PaperQuestionResponse;
import com.certifypro.question.dto.response.QuestionPaperResponse;
import com.certifypro.question.entity.PaperQuestion;
import com.certifypro.question.entity.Question;
import com.certifypro.question.entity.QuestionPaper;
import com.certifypro.question.exception.BusinessException;
import com.certifypro.question.exception.NotFoundException;
import com.certifypro.question.repository.PaperQuestionRepository;
import com.certifypro.question.repository.QuestionPaperRepository;
import com.certifypro.question.repository.QuestionRepository;
import com.certifypro.question.security.SecurityUtil;
import com.certifypro.question.service.QuestionPaperService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionPaperServiceImpl implements QuestionPaperService {

    private final QuestionPaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;

    public QuestionPaperServiceImpl(QuestionPaperRepository paperRepository,
                                    PaperQuestionRepository paperQuestionRepository,
                                    QuestionRepository questionRepository) {
        this.paperRepository = paperRepository;
        this.paperQuestionRepository = paperQuestionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public QuestionPaperResponse create(CreateQuestionPaperRequest req) {
        // windowId (exam-service) and programId (candidate-service) are cross-service
        // references. Per spec rule 2, the monolith's ExamWindow existence check is
        // dropped — accept the ids without validation.
        paperRepository.findByPaperCode(req.paperCode()).ifPresent(p -> {
            throw new BusinessException("Paper code already exists: " + req.paperCode());
        });

        QuestionPaper p = QuestionPaper.builder()
                .windowId(req.windowId())
                .programId(req.programId())
                .paperCode(req.paperCode())
                .totalMarks(0)
                .duration(req.duration())
                .instructionsRef(req.instructionsRef())
                .createdById(SecurityUtil.currentUserId())
                .status(PaperStatus.Draft)
                .build();
        p = paperRepository.save(p);
        return toResponse(p);
    }

    @Override
    public QuestionPaperResponse getById(Long id) {
        return toResponse(findPaper(id));
    }

    @Override
    @Transactional
    public QuestionPaperResponse addQuestions(Long paperId, AddPaperQuestionsRequest req) {
        QuestionPaper paper = findPaper(paperId);
        if (paper.getStatus() != PaperStatus.Draft) {
            throw new BusinessException("Questions can only be added while the paper is in Draft");
        }

        for (AddPaperQuestionsRequest.Item item : req.items()) {
            // Question is local to this service, so the relationship stays intact.
            Question question = questionRepository.findById(item.questionId())
                    .orElseThrow(() -> NotFoundException.of("Question", item.questionId()));
            PaperQuestion pq = PaperQuestion.builder()
                    .paperId(paperId)
                    .question(question)
                    .sequenceOrder(item.sequenceOrder())
                    .marksAllocated(item.marksAllocated())
                    .build();
            paperQuestionRepository.save(pq);
        }

        // Recompute total marks from all questions on the paper.
        int total = paperQuestionRepository.findByPaperIdOrderBySequenceOrderAsc(paperId).stream()
                .mapToInt(pq -> pq.getMarksAllocated() == null ? 0 : pq.getMarksAllocated())
                .sum();
        paper.setTotalMarks(total);
        paperRepository.save(paper);

        return toResponse(paper);
    }

    @Override
    @Transactional
    public QuestionPaperResponse updateStatus(Long paperId, UpdatePaperStatusRequest req) {
        QuestionPaper paper = findPaper(paperId);
        PaperStatus target = parseStatus(req.status());
        if (target.ordinal() <= paper.getStatus().ordinal()) {
            throw new BusinessException("Paper status can only move forward: "
                    + paper.getStatus() + " -> " + target + " is not allowed");
        }
        paper.setStatus(target);
        paperRepository.save(paper);
        return toResponse(paper);
    }

    @Override
    public PaperDto getInternal(Long paperId) {
        return PaperDto.from(findPaper(paperId));
    }

    private QuestionPaperResponse toResponse(QuestionPaper paper) {
        List<PaperQuestionResponse> questions = paperQuestionRepository
                .findByPaperIdOrderBySequenceOrderAsc(paper.getPaperId()).stream()
                .map(PaperQuestionResponse::from).toList();
        return QuestionPaperResponse.from(paper, questions);
    }

    private QuestionPaper findPaper(Long id) {
        return paperRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("QuestionPaper", id));
    }

    private PaperStatus parseStatus(String v) {
        try {
            return PaperStatus.valueOf(v);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + v
                    + " (Draft, Finalised, Distributed, Archived)");
        }
    }
}
