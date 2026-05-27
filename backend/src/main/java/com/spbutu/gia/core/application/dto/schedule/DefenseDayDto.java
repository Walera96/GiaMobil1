package com.spbutu.gia.core.application.dto.schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class DefenseDayDto {
    private UUID meetingId;
    private LocalDate date;
    private String location;
    private String gekName;
    private Integer totalSlots;
    private List<DefenseSlotDto> slots;

    public UUID getMeetingId() { return meetingId; }
    public void setMeetingId(UUID meetingId) { this.meetingId = meetingId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getGekName() { return gekName; }
    public void setGekName(String gekName) { this.gekName = gekName; }

    public Integer getTotalSlots() { return totalSlots; }
    public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }

    public List<DefenseSlotDto> getSlots() { return slots; }
    public void setSlots(List<DefenseSlotDto> slots) { this.slots = slots; }
}
