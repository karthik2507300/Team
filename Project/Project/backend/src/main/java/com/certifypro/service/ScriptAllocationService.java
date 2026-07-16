package com.certifypro.service;

import com.certifypro.dto.request.CreateScriptAllocationRequest;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.dto.response.ScriptAllocationResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.ScriptAllocation;
import com.certifypro.model.enums.ScriptStatus;
import com.certifypro.repository.QuestionPaperRepository;
import com.certifypro.repository.ScriptAllocationRepository;
import com.certifypro.repository.SeatAllocationRepository;
import com.certifypro.repository.UserRepository;
import com.certifypro.security.SecurityUtil;
import com.certifypro.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ScriptAllocationService {

    private final ScriptAllocationRepository scriptRepository;
    private final SeatAllocationRepository seatRepository;
    private final UserRepository userRepository;
    private final QuestionPaperRepository paperRepository;
    private final NotificationService notificationService;

    public ScriptAllocationService(ScriptAllocationRepository scriptRepository,
                                   SeatAllocationRepository seatRepository,
                                   UserRepository userRepository,
                                   QuestionPaperRepository paperRepository,
                                   NotificationService notificationService) {
        this.scriptRepository = scriptRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.paperRepository = paperRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ScriptAllocationResponse assign(CreateScriptAllocationRequest req) {
        if (!seatRepository.existsById(req.allocationId())) {
            throw NotFoundException.of("SeatAllocation", req.allocationId());
        }
        if (!userRepository.existsById(req.evaluatorId())) {
            throw NotFoundException.of("Evaluator(User)", req.evaluatorId());
        }
        if (!paperRepository.existsById(req.paperId())) {
            throw NotFoundException.of("QuestionPaper", req.paperId());
        }

        ScriptAllocation s = new ScriptAllocation();
        s.setAllocationId(req.allocationId());
        s.setEvaluatorId(req.evaluatorId());
        s.setPaperId(req.paperId());
        s.setAllocationDate(LocalDate.now());
        s.setStatus(ScriptStatus.Assigned);
        s = scriptRepository.save(s);

        notificationService.notifyUser(req.evaluatorId(), "Result",
                "You have been assigned answer scripts for paper #" + req.paperId());

        return ScriptAllocationResponse.from(s);
    }

    public PageResponse<ScriptAllocationResponse> listByEvaluator(Long evaluatorId, int page, int limit) {
        Long effective = "Evaluator".equals(SecurityUtil.currentRole())
                ? SecurityUtil.currentUserId() : evaluatorId;
        if (effective == null) {
            return PageResponse.from(scriptRepository.findAll(PageUtil.of(page, limit))
                    .map(ScriptAllocationResponse::from));
        }
        return PageResponse.from(scriptRepository.findByEvaluatorId(effective, PageUtil.of(page, limit))
                .map(ScriptAllocationResponse::from));
    }
}
