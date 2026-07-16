package com.certifypro.result.service.impl;

import com.certifypro.result.client.NotificationServiceGateway;
import com.certifypro.result.common.ScriptStatus;
import com.certifypro.result.dto.request.CreateScriptAllocationRequest;
import com.certifypro.result.dto.response.PageResponse;
import com.certifypro.result.dto.response.ScriptAllocationResponse;
import com.certifypro.result.entity.ScriptAllocation;
import com.certifypro.result.repository.ScriptAllocationRepository;
import com.certifypro.result.security.SecurityUtil;
import com.certifypro.result.service.ScriptAllocationService;
import com.certifypro.result.util.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class ScriptAllocationServiceImpl implements ScriptAllocationService {

    private final ScriptAllocationRepository scriptRepository;
    private final NotificationServiceGateway notificationGateway;

    public ScriptAllocationServiceImpl(ScriptAllocationRepository scriptRepository,
                                       NotificationServiceGateway notificationGateway) {
        this.scriptRepository = scriptRepository;
        this.notificationGateway = notificationGateway;
    }

    /**
     * Assign answer scripts to an evaluator.
     * <p>Simplification vs. monolith: the monolith validated seat allocation,
     * evaluator (user) and paper existence against local repositories. Those rows
     * now live in other services (exam-service, auth-service, question-service);
     * per the build spec pure existence-only FK validation is dropped and the ids
     * are accepted as-is. The user-visible side effect (notifying the evaluator)
     * is preserved via the notification-service gateway.
     */
    @Override
    @Transactional
    public ScriptAllocationResponse assign(CreateScriptAllocationRequest req) {
        ScriptAllocation s = ScriptAllocation.builder()
                .allocationId(req.allocationId())
                .evaluatorId(req.evaluatorId())
                .paperId(req.paperId())
                .allocationDate(LocalDate.now())
                .status(ScriptStatus.Assigned)
                .build();
        s = scriptRepository.save(s);

        notificationGateway.notifyUser(req.evaluatorId(), "Result",
                "You have been assigned answer scripts for paper #" + req.paperId());

        return ScriptAllocationResponse.from(s);
    }

    @Override
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
