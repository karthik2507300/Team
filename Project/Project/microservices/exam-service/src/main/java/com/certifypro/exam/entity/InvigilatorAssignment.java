package com.certifypro.exam.entity;

import com.certifypro.exam.common.InvigilatorStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invigilator_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvigilatorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    /** Intra-service relationship. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "window_id")
    private ExamWindow examWindow;

    /** Intra-service relationship. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "centre_id")
    private TestCentre testCentre;

    /** Cross-service reference to auth-service user — stays a plain id (no FK). */
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_number")
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvigilatorStatus status;
}
