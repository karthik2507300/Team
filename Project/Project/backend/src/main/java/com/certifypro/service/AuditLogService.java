package com.certifypro.service;

import com.certifypro.dto.response.AuditLogResponse;
import com.certifypro.dto.response.PageResponse;
import com.certifypro.repository.AuditLogRepository;
import com.certifypro.util.PageUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public PageResponse<AuditLogResponse> search(Long userId, String module,
                                                 LocalDate from, LocalDate to,
                                                 int page, int limit) {
        LocalDateTime fromTs = from == null ? null : from.atStartOfDay();
        LocalDateTime toTs = to == null ? null : to.atTime(LocalTime.MAX);
        return PageResponse.from(
                auditLogRepository.search(userId, module, fromTs, toTs, PageUtil.of(page, limit))
                        .map(AuditLogResponse::from));
    }
}
