package com.certifypro.util;

import com.certifypro.model.AuditLog;
import com.certifypro.repository.AuditLogRepository;
import com.certifypro.security.SecurityUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Writes an audit trail entry for critical create/update actions.
 * Invoked explicitly from service methods that mutate critical tables
 * (User, MarksEntry, CandidateResult, Certificate, RenewalApplication,
 *  ReEvaluationRequest). Captures the acting user from the security context.
 */
@Component
public class AuditLogUtil {

    private final AuditLogRepository auditLogRepository;

    public AuditLogUtil(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /** Logs using the currently authenticated user as the actor. */
    public void log(String action, String module, Object entityId) {
        log(SecurityUtil.currentUserId(), action, module, entityId);
    }

    public void log(Long actorUserId, String action, String module, Object entityId) {
        AuditLog entry = new AuditLog();
        entry.setUserId(actorUserId);
        entry.setAction(action);
        entry.setModule(module);
        entry.setEntityId(entityId == null ? null : String.valueOf(entityId));
        entry.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(entry);
    }
}
