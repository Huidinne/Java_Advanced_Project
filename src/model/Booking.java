package model;

import java.time.LocalDateTime;

public class Booking {
    private int id;
    private int userId;
    private int roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private Integer supportStaffId;
    private PreparationStatus preparationStatus;

    public Booking() {}

    public Booking(int id, int userId, int roomId,
                   LocalDateTime startTime, LocalDateTime endTime,
                   BookingStatus status, Integer supportStaffId,
                   PreparationStatus preparationStatus) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.supportStaffId = supportStaffId;
        this.preparationStatus = preparationStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getSupportStaffId() {
        return supportStaffId;
    }

    public void setSupportStaffId(Integer supportStaffId) {
        this.supportStaffId = supportStaffId;
    }

    public PreparationStatus getPreparationStatus() {
        return preparationStatus;
    }

    public void setPreparationStatus(PreparationStatus preparationStatus) {
        this.preparationStatus = preparationStatus;
    }
}
