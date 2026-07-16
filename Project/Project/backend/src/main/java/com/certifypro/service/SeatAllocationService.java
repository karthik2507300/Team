package com.certifypro.service;

import com.certifypro.dto.request.CreateSeatAllocationRequest;
import com.certifypro.dto.request.UpdateAllocationStatusRequest;
import com.certifypro.dto.response.SeatAllocationResponse;
import com.certifypro.exception.BusinessException;
import com.certifypro.exception.NotFoundException;
import com.certifypro.model.*;
import com.certifypro.model.enums.EligibilityStatus;
import com.certifypro.model.enums.SeatStatus;
import com.certifypro.model.enums.TestCentreStatus;
import com.certifypro.repository.*;
import com.certifypro.security.SecurityUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SeatAllocationService {

    private final SeatAllocationRepository seatRepository;
    private final ExamWindowRepository examWindowRepository;
    private final TestCentreRepository testCentreRepository;
    private final CandidateRepository candidateRepository;
    private final ProgramEnrolmentRepository enrolmentRepository;
    private final CertificationProgramRepository programRepository;
    private final com.certifypro.util.PdfGenerator pdfGenerator;
    private final NotificationService notificationService;

    public SeatAllocationService(SeatAllocationRepository seatRepository,
                                 ExamWindowRepository examWindowRepository,
                                 TestCentreRepository testCentreRepository,
                                 CandidateRepository candidateRepository,
                                 ProgramEnrolmentRepository enrolmentRepository,
                                 CertificationProgramRepository programRepository,
                                 com.certifypro.util.PdfGenerator pdfGenerator,
                                 NotificationService notificationService) {
        this.seatRepository = seatRepository;
        this.examWindowRepository = examWindowRepository;
        this.testCentreRepository = testCentreRepository;
        this.candidateRepository = candidateRepository;
        this.enrolmentRepository = enrolmentRepository;
        this.programRepository = programRepository;
        this.pdfGenerator = pdfGenerator;
        this.notificationService = notificationService;
    }

    @Transactional
    public SeatAllocationResponse allocate(CreateSeatAllocationRequest req) {
        Long candidateId = resolveCandidateId(req.candidateId());

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> NotFoundException.of("Candidate", candidateId));
        ExamWindow window = examWindowRepository.findById(req.windowId())
                .orElseThrow(() -> NotFoundException.of("ExamWindow", req.windowId()));
        TestCentre centre = testCentreRepository.findById(req.centreId())
                .orElseThrow(() -> NotFoundException.of("TestCentre", req.centreId()));

        if (centre.getStatus() != TestCentreStatus.Active) {
            throw new BusinessException("Test centre is not active");
        }

        // Business rule 1: eligibility + attempts (against the window's program enrolment).
        ProgramEnrolment enrolment = enrolmentRepository
                .findByCandidateIdAndProgramId(candidateId, window.getProgramId())
                .orElseThrow(() -> new BusinessException(
                        "Candidate is not enrolled in the program for this exam window"));
        if (enrolment.getEligibilityStatus() != EligibilityStatus.Eligible) {
            throw new BusinessException("Candidate is not eligible (status: "
                    + enrolment.getEligibilityStatus() + ")");
        }
        Integer used = enrolment.getAttemptsUsed() == null ? 0 : enrolment.getAttemptsUsed();
        if (enrolment.getMaxAttempts() != null && used >= enrolment.getMaxAttempts()) {
            throw new BusinessException("Maximum attempts reached for this program");
        }

        // Business rule 2: centre capacity for this window.
        long occupied = seatRepository.countByWindowIdAndCentreId(req.windowId(), req.centreId());
        if (occupied >= centre.getCapacity()) {
            throw new BusinessException("Test centre capacity is full for this exam window");
        }

        SeatAllocation alloc = new SeatAllocation();
        alloc.setCandidateId(candidateId);
        alloc.setWindowId(req.windowId());
        alloc.setCentreId(req.centreId());
        alloc.setRoomNumber(req.roomNumber() != null ? req.roomNumber() : "R1");
        alloc.setSeatNumber(req.seatNumber() != null ? req.seatNumber() : "S" + (occupied + 1));
        alloc.setStatus(SeatStatus.Allocated);
        alloc = seatRepository.save(alloc);

        // Unique hall ticket number derived from the generated id.
        alloc.setHallTicketNumber("HT-" + req.windowId() + "-" + alloc.getAllocationId());
        alloc = seatRepository.save(alloc);

        // Business rule 8: increment attempts used on the enrolment.
        enrolment.setAttemptsUsed(used + 1);
        enrolmentRepository.save(enrolment);

        if (candidate.getUserId() != null) {
            notificationService.notifyUser(candidate.getUserId(), "Exam",
                    "Seat allocated. Your hall ticket " + alloc.getHallTicketNumber() + " is now available.");
        }

        return SeatAllocationResponse.from(alloc);
    }

    public List<SeatAllocationResponse> listByWindowCentre(Long windowId, Long centreId) {
        List<SeatAllocation> rows;
        if (windowId != null && centreId != null) {
            rows = seatRepository.findByWindowIdAndCentreId(windowId, centreId);
        } else if (windowId != null) {
            rows = seatRepository.findByWindowId(windowId);
        } else if (centreId != null) {
            rows = seatRepository.findByCentreId(centreId);
        } else {
            rows = seatRepository.findAll();
        }
        return rows.stream().map(SeatAllocationResponse::from).toList();
    }

    public List<SeatAllocationResponse> getByCandidate(Long candidateId) {
        assertCanAccessCandidate(candidateId);
        return seatRepository.findByCandidateId(candidateId).stream()
                .map(SeatAllocationResponse::from).toList();
    }

    @Transactional
    public SeatAllocationResponse updateStatus(Long id, UpdateAllocationStatusRequest req) {
        SeatAllocation alloc = findAllocation(id);
        alloc.setStatus(parseStatus(req.status()));
        return SeatAllocationResponse.from(seatRepository.save(alloc));
    }

    public byte[] generateHallTicket(Long allocationId) {
        SeatAllocation alloc = findAllocation(allocationId);
        assertCanAccessCandidate(alloc.getCandidateId());

        Candidate candidate = candidateRepository.findById(alloc.getCandidateId()).orElse(null);
        ExamWindow window = examWindowRepository.findById(alloc.getWindowId()).orElse(null);
        TestCentre centre = testCentreRepository.findById(alloc.getCentreId()).orElse(null);
        CertificationProgram program = window == null ? null
                : programRepository.findById(window.getProgramId()).orElse(null);

        return pdfGenerator.generateHallTicket(alloc, candidate, window, centre, program);
    }

    private SeatAllocation findAllocation(Long id) {
        return seatRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("SeatAllocation", id));
    }

    private Long resolveCandidateId(Long requested) {
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            return candidateRepository.findByUserId(SecurityUtil.currentUserId())
                    .orElseThrow(() -> new BusinessException("Complete your candidate profile first"))
                    .getCandidateId();
        }
        if (requested == null) {
            throw new IllegalArgumentException("candidateId is required when allocating as staff");
        }
        return requested;
    }

    private void assertCanAccessCandidate(Long candidateId) {
        if ("Candidate".equals(SecurityUtil.currentRole())) {
            Candidate own = candidateRepository.findByUserId(SecurityUtil.currentUserId()).orElse(null);
            if (own == null || !Objects.equals(own.getCandidateId(), candidateId)) {
                throw new AccessDeniedException("You can only access your own seat allocations");
            }
        }
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
