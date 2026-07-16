package com.certifypro.result.service;

import com.certifypro.result.dto.request.CreateScriptAllocationRequest;
import com.certifypro.result.dto.response.PageResponse;
import com.certifypro.result.dto.response.ScriptAllocationResponse;

public interface ScriptAllocationService {

    ScriptAllocationResponse assign(CreateScriptAllocationRequest req);

    PageResponse<ScriptAllocationResponse> listByEvaluator(Long evaluatorId, int page, int limit);
}
