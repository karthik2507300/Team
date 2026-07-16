package com.certifypro.analytics.service;

import com.certifypro.analytics.dto.request.GenerateReportRequest;
import com.certifypro.analytics.dto.response.PageResponse;
import com.certifypro.analytics.dto.response.ReportResponse;

public interface ReportService {

    ReportResponse generate(GenerateReportRequest req);

    PageResponse<ReportResponse> list(String scope, int page, int limit);
}
