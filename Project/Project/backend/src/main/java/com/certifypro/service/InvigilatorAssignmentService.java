package com.certifypro.service;

import com.certifypro.dto.request.CreateInvigilatorAssignmentRequest;
import com.certifypro.dto.response.InvigilatorAssignmentResponse;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.InvigilatorAssignment;
import com.certifypro.model.enums.InvigilatorStatus;
import com.certifypro.repository.ExamWindowRepository;
import com.certifypro.repository.InvigilatorAssignmentRepository;
import com.certifypro.repository.TestCentreRepository;
import com.certifypro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvigilatorAssignmentService {

    private final InvigilatorAssignmentRepository assignmentRepository;
    private final ExamWindowRepository examWindowRepository;
    private final TestCentreRepository testCentreRepository;
    private final UserRepository userRepository;

    public InvigilatorAssignmentService(InvigilatorAssignmentRepository assignmentRepository,
                                        ExamWindowRepository examWindowRepository,
                                        TestCentreRepository testCentreRepository,
                                        UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.examWindowRepository = examWindowRepository;
        this.testCentreRepository = testCentreRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InvigilatorAssignmentResponse assign(CreateInvigilatorAssignmentRequest req) {
        if (!examWindowRepository.existsById(req.windowId())) {
            throw NotFoundException.of("ExamWindow", req.windowId());
        }
        if (!testCentreRepository.existsById(req.centreId())) {
            throw NotFoundException.of("TestCentre", req.centreId());
        }
        if (!userRepository.existsById(req.userId())) {
            throw NotFoundException.of("User", req.userId());
        }

        InvigilatorAssignment a = new InvigilatorAssignment();
        a.setWindowId(req.windowId());
        a.setCentreId(req.centreId());
        a.setUserId(req.userId());
        a.setRoomNumber(req.roomNumber());
        a.setStatus(InvigilatorStatus.Assigned);
        return InvigilatorAssignmentResponse.from(assignmentRepository.save(a));
    }

    public List<InvigilatorAssignmentResponse> list(Long windowId, Long centreId) {
        List<InvigilatorAssignment> result;
        if (windowId != null && centreId != null) {
            result = assignmentRepository.findByWindowIdAndCentreId(windowId, centreId);
        } else if (windowId != null) {
            result = assignmentRepository.findByWindowId(windowId);
        } else {
            result = assignmentRepository.findAll();
        }
        return result.stream().map(InvigilatorAssignmentResponse::from).toList();
    }
}
