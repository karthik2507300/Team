package com.certifypro.auth.util;

import com.certifypro.auth.entity.AuditLog;
import com.certifypro.auth.repository.AuditLogRepository;
import com.certifypro.auth.security.SecurityUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** Writes an audit-trail entry for critical create/update actions. */
@Component
public class AuditLogUtil {

    private final AuditLogRepository auditLogRepository;

    public AuditLogUtil(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String module, Object entityId) {
        log(SecurityUtil.currentUserId(), action, module, entityId);
    }

    public void log(Long actorUserId, String action, String module, Object entityId) {
        auditLogRepository.save(AuditLog.builder()
                .userId(actorUserId)
                .action(action)
                .module(module)
                .entityId(entityId == null ? null : String.valueOf(entityId))
                .timestamp(LocalDateTime.now())
                .build());
    }
}
