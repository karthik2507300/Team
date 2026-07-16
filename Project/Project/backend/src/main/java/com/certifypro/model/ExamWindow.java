package com.certifypro.model;

import com.certifypro.model.enums.ExamWindowStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "exam_window")
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

    public ExamWindow() {
    }

    public Long getWindowId() {
        return windowId;
    }

    public void setWindowId(Long windowId) {
        this.windowId = windowId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public LocalDate getResultDate() {
        return resultDate;
    }

    public void setResultDate(LocalDate resultDate) {
        this.resultDate = resultDate;
    }

    public ExamWindowStatus getStatus() {
        return status;
    }

    public void setStatus(ExamWindowStatus status) {
        this.status = status;
    }
}
