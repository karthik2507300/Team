package com.certifypro.service;

import com.certifypro.dto.request.AddPaperQuestionsRequest;
import com.certifypro.dto.request.CreateQuestionPaperRequest;
import com.certifypro.dto.request.UpdatePaperStatusRequest;
import com.certifypro.dto.response.PaperQuestionResponse;
import com.certifypro.dto.response.QuestionPaperResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.PaperQuestion;
import com.certifypro.model.QuestionPaper;
import com.certifypro.model.enums.PaperStatus;
import com.certifypro.repository.ExamWindowRepository;
import com.certifypro.repository.PaperQuestionRepository;
import com.certifypro.repository.QuestionPaperRepository;
import com.certifypro.repository.QuestionRepository;
import com.certifypro.security.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionPaperService {

    private final QuestionPaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final QuestionRepository questionRepository;
    private final ExamWindowRepository examWindowRepository;

    public QuestionPaperService(QuestionPaperRepository paperRepository,
                                PaperQuestionRepository paperQuestionRepository,
                                QuestionRepository questionRepository,
                                ExamWindowRepository examWindowRepository) {
        this.paperRepository = paperRepository;
        this.paperQuestionRepository = paperQuestionRepository;
        this.questionRepository = questionRepository;
        this.examWindowRepository = examWindowRepository;
    }

    @Transactional
    public QuestionPaperResponse create(CreateQuestionPaperRequest req) {
        if (!examWindowRepository.existsById(req.windowId())) {
            throw NotFoundException.of("ExamWindow", req.windowId());
        }
        paperRepository.findByPaperCode(req.paperCode()).ifPresent(p -> {
            throw new BusinessException("Paper code already exists: " + req.paperCode());
        });

        QuestionPaper p = new QuestionPaper();
        p.setWindowId(req.windowId());
        p.setProgramId(req.programId());
        p.setPaperCode(req.paperCode());
        p.setTotalMarks(0);
        p.setDuration(req.duration());
        p.setInstructionsRef(req.instructionsRef());
        p.setCreatedById(SecurityUtil.currentUserId());
        p.setStatus(PaperStatus.Draft);
        p = paperRepository.save(p);
        return toResponse(p);
    }

    public QuestionPaperResponse getById(Long id) {
        return toResponse(findPaper(id));
    }

    @Transactional
    public QuestionPaperResponse addQuestions(Long paperId, AddPaperQuestionsRequest req) {
        QuestionPaper paper = findPaper(paperId);
        if (paper.getStatus() != PaperStatus.Draft) {
            throw new BusinessException("Questions can only be added while the paper is in Draft");
        }

        for (AddPaperQuestionsRequest.Item item : req.items()) {
            if (!questionRepository.existsById(item.questionId())) {
                throw NotFoundException.of("Question", item.questionId());
            }
            PaperQuestion pq = new PaperQuestion();
            pq.setPaperId(paperId);
            pq.setQuestionId(item.questionId());
            pq.setSequenceOrder(item.sequenceOrder());
            pq.setMarksAllocated(item.marksAllocated());
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
