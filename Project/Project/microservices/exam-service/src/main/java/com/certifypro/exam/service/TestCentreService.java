package com.certifypro.exam.service;

import com.certifypro.exam.dto.request.CreateTestCentreRequest;
import com.certifypro.exam.dto.request.UpdateTestCentreRequest;
import com.certifypro.exam.dto.response.PageResponse;
import com.certifypro.exam.dto.response.TestCentreResponse;

public interface TestCentreService {

    TestCentreResponse create(CreateTestCentreRequest req);

    PageResponse<TestCentreResponse> list(int page, int limit);

    TestCentreResponse update(Long id, UpdateTestCentreRequest req);
}
