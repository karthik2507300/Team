package com.certifypro.result.service;

import com.certifypro.result.dto.request.CreateMarksEntryRequest;
import com.certifypro.result.dto.response.MarksEntryResponse;
import com.certifypro.result.dto.response.PageResponse;

public interface MarksEntryService {

    MarksEntryResponse submit(CreateMarksEntryRequest req);

    PageResponse<MarksEntryResponse> list(String status, Long scriptId, int page, int limit);

    MarksEntryResponse verify(Long id);

    MarksEntryResponse moderate(Long id);
}
