package com.certifypro.exam.service.impl;

import com.certifypro.exam.client.CandidateProfileGateway;
import com.certifypro.exam.client.NotificationGateway;
import com.certifypro.exam.client.dto.CandidateDto;
import com.certifypro.exam.client.dto.ProgramDto;
import com.certifypro.exam.common.SeatStatus;
import com.certifypro.exam.common.TestCentreStatus;
import com.certifypro.exam.dto.request.CreateSeatAllocationRequest;
import com.certifypro.exam.dto.request.UpdateAllocationStatusRequest;
import com.certifypro.exam.dto.response.SeatAllocationInternalDto;
import com.certifypro.exam.dto.response.SeatAllocationResponse;
import com.certifypro.exam.entity.ExamWindow;
import com.certifypro.exam.entity.SeatAllocation;
import com.certifypro.exam.entity.TestCentre;
import com.certifypro.exam.exception.BusinessException;
import com.certifypro.exam.exception.NotFoundException;
import com.certifypro.exam.repository.ExamWindowRepository;
import com.certifypro.exam.repository.SeatAllocationRepository;
import com.certifypro.exam.repository.TestCentreRepository;
import com.certifypro.exam.service.SeatAllocationService;
import com.certifypro.exam.util.PdfGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatAllocationServiceImpl implements SeatAllocationService {

    private final SeatAllocationRepository seatRepository;
    private final ExamWindowRepository examWindowRepository;
    private final TestCentreRepository testCentreRepository;
    private final PdfGenerator pdfGenerator;
    private final CandidateProfileGateway candidateGateway;
    private final NotificationGateway notificationGateway;

    public SeatAllocationServiceImpl(SeatAllocationRepository seatRepository,
                                     ExamWindowRepository examWindowRepository,
                                     TestCentreRepository testCentreRepository,
                                     PdfGenerator pdfGenerator,
                                     CandidateProfileGateway candidateGateway,
                                     NotificationGateway notificationGateway) {
        this.seatRepository = seatRepository;
        this.examWindowRepository = examWindowRepository;
        this.testCentreRepository = testCentreRepository;
        this.pdfGenerator = pdfGenerator;
        this.candidateGateway = candidateGateway;
        this.notificationGateway = notificationGateway;
    }

    @Override
    @Transactional
    public SeatAllocationResponse allocate(CreateSeatAllocationRequest req) {
        // SIMPLIFIED: candidateId is now a required, plain cross-service id.
        // The monolith derived it from the Candidate profile for self-registration
        // and enforced enrolment eligibility / attempt limits via candidate-service
        // repositories; those cross-service existence + eligibility checks are dropped
        // per spec rule 2 (exam-service does not own candidate/enrolment data).
        if (req.candidateId() == null) {
            throw new IllegalArgumentException("candidateId is required");
        }

        ExamWindow window = examWindowRepository.findById(req.windowId())
                .orElseThrow(() -> NotFoundException.of("ExamWindow", req.windowId()));
        TestCentre centre = testCentreRepository.findById(req.centreId())
                .orElseThrow(() -> NotFoundException.of("TestCentre", req.centreId()));

        if (centre.getStatus() != TestCentreStatus.Active) {
            throw new BusinessException("Test centre is not active");
        }

        // Business rule: centre capacity for this window (intra-service data — kept).
        long occupied = seatRepository
                .countByExamWindow_WindowIdAndTestCentre_CentreId(req.windowId(), req.centreId());
        if (centre.getCapacity() != null && occupied >= centre.getCapacity()) {
            throw new BusinessException("Test centre capacity is full for this exam window");
        }

        SeatAllocation alloc = SeatAllocation.builder()
                .candidateId(req.candidateId())
                .examWindow(window)
                .testCentre(centre)
                .roomNumber(req.roomNumber() != null ? req.roomNumber() : "R1")
                .seatNumber(req.seatNumber() != null ? req.seatNumber() : "S" + (occupied + 1))
                .status(SeatStatus.Allocated)
                .build();
        alloc = seatRepository.save(alloc);

        // Unique hall ticket number derived from the generated id.
        alloc.setHallTicketNumber("HT-" + req.windowId() + "-" + alloc.getAllocationId());
        alloc = seatRepository.save(alloc);

        // Cross-service DATA flow (kept): look up the candidate's userId and notify.
        CandidateDto candidate = candidateGateway.getCandidate(req.candidateId());
        if (candidate != null && candidate.userId() != null) {
            notificationGateway.notifyUser(candidate.userId(), "Exam",
                    "Seat allocated. Your hall ticket " + alloc.getHallTicketNumber()
                            + " is now available.");
        }

        return SeatAllocationResponse.from(alloc);
    }

    @Override
    public List<SeatAllocationResponse> listByWindowCentre(Long windowId, Long centreId) {
        List<SeatAllocation> rows;
        if (windowId != null && centreId != null) {
            rows = seatRepository.findByExamWindow_WindowIdAndTestCentre_CentreId(windowId, centreId);
        } else if (windowId != null) {
            rows = seatRepository.findByExamWindow_WindowId(windowId);
        } else if (centreId != null) {
            rows = seatRepository.findByTestCentre_CentreId(centreId);
        } else {
            rows = seatRepository.findAll();
        }
        return rows.stream().map(SeatAllocationResponse::from).toList();
    }

    @Override
    public List<SeatAllocationResponse> getByCandidate(Long candidateId) {
        // SIMPLIFIED: monolith restricted a Candidate caller to their own allocations by
        // resolving their candidateId from the local Candidate profile. That ownership
        // check required candidate-service data and is dropped; the endpoint's
        // @PreAuthorize still governs access.
        return seatRepository.findByCandidateId(candidateId).stream()
                .map(SeatAllocationResponse::from).toList();
    }

    @Override
    @Transactional
    public SeatAllocationResponse updateStatus(Long id, UpdateAllocationStatusRequest req) {
        SeatAllocation alloc = findAllocation(id);
        alloc.setStatus(parseStatus(req.status()));
        return SeatAllocationResponse.from(seatRepository.save(alloc));
    }

    @Override
    public byte[] generateHallTicket(Long allocationId) {
        SeatAllocation alloc = findAllocation(allocationId);

        ExamWindow window = alloc.getExamWindow();
        TestCentre centre = alloc.getTestCentre();

        // Cross-service DATA flow (kept): candidate name + program name for the PDF.
        CandidateDto candidate = candidateGateway.getCandidate(alloc.getCandidateId());
        ProgramDto program = window == null || window.getProgramId() == null ? null
                : candidateGateway.getProgram(window.getProgramId());

        return pdfGenerator.generateHallTicket(alloc, candidate, window, centre, program);
    }

    @Override
    public SeatAllocationInternalDto getInternal(Long allocationId) {
        return SeatAllocationInternalDto.from(findAllocation(allocationId));
    }

    private SeatAllocation findAllocation(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("SeatAllocation", id));
    }

    private SeatStatus parseStatus(String value) {
        try {
            return SeatStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + value
                    + " (allowed: Allocated, Confirmed, Cancelled, NoShow)");
        }
    }
}
