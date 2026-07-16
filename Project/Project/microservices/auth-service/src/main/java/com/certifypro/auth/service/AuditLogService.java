package com.certifypro.auth.service;

import com.certifypro.auth.dto.response.AuditLogResponse;
import com.certifypro.auth.dto.response.PageResponse;

import java.time.LocalDate;

public interface AuditLogService {

    PageResponse<AuditLogResponse> search(Long userId, String module,
                                          LocalDate from, LocalDate to, int page, int limit);
}
