package com.certifypro.exam.service.impl;

import com.certifypro.exam.common.InvigilatorStatus;
import com.certifypro.exam.dto.request.CreateInvigilatorAssignmentRequest;
import com.certifypro.exam.dto.response.InvigilatorAssignmentResponse;
import com.certifypro.exam.entity.ExamWindow;
import com.certifypro.exam.entity.InvigilatorAssignment;
import com.certifypro.exam.entity.TestCentre;
import com.certifypro.exam.exception.NotFoundException;
import com.certifypro.exam.repository.ExamWindowRepository;
import com.certifypro.exam.repository.InvigilatorAssignmentRepository;
import com.certifypro.exam.repository.TestCentreRepository;
import com.certifypro.exam.service.InvigilatorAssignmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvigilatorAssignmentServiceImpl implements InvigilatorAssignmentService {

    private final InvigilatorAssignmentRepository assignmentRepository;
    private final ExamWindowRepository examWindowRepository;
    private final TestCentreRepository testCentreRepository;

    public InvigilatorAssignmentServiceImpl(InvigilatorAssignmentRepository assignmentRepository,
                                            ExamWindowRepository examWindowRepository,
                                            TestCentreRepository testCentreRepository) {
        this.assignmentRepository = assignmentRepository;
        this.examWindowRepository = examWindowRepository;
        this.testCentreRepository = testCentreRepository;
    }

    @Override
    @Transactional
    public InvigilatorAssignmentResponse assign(CreateInvigilatorAssignmentRequest req) {
        // Window + centre are intra-service (real relationships) — existence kept.
        ExamWindow window = examWindowRepository.findById(req.windowId())
                .orElseThrow(() -> NotFoundException.of("ExamWindow", req.windowId()));
        TestCentre centre = testCentreRepository.findById(req.centreId())
                .orElseThrow(() -> NotFoundException.of("TestCentre", req.centreId()));
        // SIMPLIFIED: monolith verified the User existed via the user repository.
        // userId is now accepted as a plain cross-service id (spec rule 2).

        InvigilatorAssignment a = InvigilatorAssignment.builder()
                .examWindow(window)
                .testCentre(centre)
                .userId(req.userId())
                .roomNumber(req.roomNumber())
                .status(InvigilatorStatus.Assigned)
                .build();
        return InvigilatorAssignmentResponse.from(assignmentRepository.save(a));
    }

    @Override
    public List<InvigilatorAssignmentResponse> list(Long windowId, Long centreId) {
        List<InvigilatorAssignment> result;
        if (windowId != null && centreId != null) {
            result = assignmentRepository.findByExamWindow_WindowIdAndTestCentre_CentreId(windowId, centreId);
        } else if (windowId != null) {
            result = assignmentRepository.findByExamWindow_WindowId(windowId);
        } else {
            result = assignmentRepository.findAll();
        }
        return result.stream().map(InvigilatorAssignmentResponse::from).toList();
    }
}
