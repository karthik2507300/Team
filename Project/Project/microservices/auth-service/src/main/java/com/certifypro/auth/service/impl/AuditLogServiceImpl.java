package com.certifypro.auth.service.impl;

import com.certifypro.auth.dto.response.AuditLogResponse;
import com.certifypro.auth.dto.response.PageResponse;
import com.certifypro.auth.repository.AuditLogRepository;
import com.certifypro.auth.service.AuditLogService;
import com.certifypro.auth.util.PageUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public PageResponse<AuditLogResponse> search(Long userId, String module,
                                                 LocalDate from, LocalDate to, int page, int limit) {
        LocalDateTime fromTs = from == null ? null : from.atStartOfDay();
        LocalDateTime toTs = to == null ? null : to.atTime(LocalTime.MAX);
        return PageResponse.from(
                auditLogRepository.search(userId, module, fromTs, toTs, PageUtil.of(page, limit))
                        .map(AuditLogResponse::from));
    }
}
