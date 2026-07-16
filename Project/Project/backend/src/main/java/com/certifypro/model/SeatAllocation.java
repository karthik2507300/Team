package com.certifypro.model;

import com.certifypro.model.enums.SeatStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "seat_allocation")
public class SeatAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "allocation_id")
    private Long allocationId;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "window_id")
    private Long windowId;

    @Column(name = "centre_id")
    private Long centreId;

    @Column(name = "room_number")
    private String roomNumber;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "hall_ticket_number")
    private String hallTicketNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SeatStatus status;

    public SeatAllocation() {
    }

    public Long getAllocationId() {
        return allocationId;
    }

    public void setAllocationId(Long allocationId) {
        this.allocationId = allocationId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getHallTicketNumber() {
        return hallTicketNumber;
    }

    public void setHallTicketNumber(String hallTicketNumber) {
        this.hallTicketNumber = hallTicketNumber;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}
