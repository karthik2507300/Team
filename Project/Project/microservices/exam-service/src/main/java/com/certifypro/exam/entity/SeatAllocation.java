package com.certifypro.exam.entity;

import com.certifypro.exam.common.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seat_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    /** Cross-service reference to candidate-service — stays a plain id (no FK). */
    @Column(name = "candidate_id")
    private Long candidateId;

    /** Intra-service relationship. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "window_id")
    private ExamWindow examWindow;

    /** Intra-service relationship. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centre_id")
    private TestCentre testCentre;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "hall_ticket_number")
    private String hallTicketNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SeatStatus status;
}
