package com.certifypro.auth.dto.response;

import com.certifypro.auth.entity.AuditLog;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long auditId,
        Long userId,
        String action,
        String module,
        String entityId,
        LocalDateTime timestamp
) {
    public static AuditLogResponse from(AuditLog a) {
        return new AuditLogResponse(
                a.getAuditId(), a.getUserId(), a.getAction(),
                a.getModule(), a.getEntityId(), a.getTimestamp());
    }
}
