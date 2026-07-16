package com.certifypro.model;

import com.certifypro.model.enums.InvigilatorStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "invigilator_assignment")
public class InvigilatorAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "window_id")
    private Long windowId;

    @Column(name = "centre_id")
    private Long centreId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_number")
    private String roomNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvigilatorStatus status;

    public InvigilatorAssignment() {
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getWindowId() {
        return windowId;
    }

    public void setWindowId(Long windowId) {
        this.windowId = windowId;
    }

    public Long getCentreId() {
        return centreId;
    }

    public void setCentreId(Long centreId) {
        this.centreId = centreId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public InvigilatorStatus getStatus() {
        return status;
    }

    public void setStatus(InvigilatorStatus status) {
        this.status = status;
    }
}
