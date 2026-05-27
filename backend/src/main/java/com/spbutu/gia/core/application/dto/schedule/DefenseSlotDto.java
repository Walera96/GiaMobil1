package com.spbutu.gia.core.application.dto.schedule;

import java.time.LocalDateTime;
import java.util.UUID;

public class DefenseSlotDto {
    private UUID studentId;
    private String studentName;
    private String recordBookNumber;
    private String thesisTopic;
    private String supervisorName;
    private Integer orderNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private Integer presentationDuration;

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRecordBookNumber() { return recordBookNumber; }
    public void setRecordBookNumber(String recordBookNumber) { this.recordBookNumber = recordBookNumber; }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public Integer getOrderNumber() { return orderNumber; }
    public void setOrderNumber(Integer orderNumber) { this.orderNumber = orderNumber; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getPresentationDuration() { return presentationDuration; }
    public void setPresentationDuration(Integer presentationDuration) { this.presentationDuration = presentationDuration; }
}
