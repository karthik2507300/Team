package com.certifypro.auth.controller;

import com.certifypro.auth.dto.response.ApiResponse;
import com.certifypro.auth.dto.response.AuditLogResponse;
import com.certifypro.auth.dto.response.PageResponse;
import com.certifypro.auth.security.SecurityUtil;
import com.certifypro.auth.service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Admin: full audit trail. Non-admin: own activity only (userId forced to caller).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> search(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        boolean isAdmin = "Admin".equals(SecurityUtil.currentRole());
        Long effectiveUserId = isAdmin ? userId : SecurityUtil.currentUserId();

        return ResponseEntity.ok(ApiResponse.ok(
                auditLogService.search(effectiveUserId, module, from, to, page, limit)));
    }
}
