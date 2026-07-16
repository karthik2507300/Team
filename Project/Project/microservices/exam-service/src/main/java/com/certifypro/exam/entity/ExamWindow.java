package com.certifypro.exam.entity;

import com.certifypro.exam.common.ExamWindowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exam_window")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamWindow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "window_id")
    private Long windowId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "exam_name")
    private String examName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    @Column(name = "result_date")
    private LocalDate resultDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ExamWindowStatus status;
}
