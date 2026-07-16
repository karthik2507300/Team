package com.certifypro.exam.service;

import com.certifypro.exam.dto.request.CreateInvigilatorAssignmentRequest;
import com.certifypro.exam.dto.response.InvigilatorAssignmentResponse;

import java.util.List;

public interface InvigilatorAssignmentService {

    InvigilatorAssignmentResponse assign(CreateInvigilatorAssignmentRequest req);

    List<InvigilatorAssignmentResponse> list(Long windowId, Long centreId);
}
