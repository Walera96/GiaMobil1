package com.spbutu.gia.core.application.dto.schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class DefenseScheduleRequestDto {
    private UUID groupId;
    private UUID directionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime dayStartTime;
    private LocalTime dayEndTime;
    private Integer slotDurationMinutes;
    private Integer breakDurationMinutes;
    private String location;
    private UUID gekId;
    private List<String> locations;
    private String sortBy; // ALPHABETIC | AVG_SCORE | RANDOM

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public UUID getDirectionId() { return directionId; }
    public void setDirectionId(UUID directionId) { this.directionId = directionId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalTime getDayStartTime() { return dayStartTime; }
    public void setDayStartTime(LocalTime dayStartTime) { this.dayStartTime = dayStartTime; }

    public LocalTime getDayEndTime() { return dayEndTime; }
    public void setDayEndTime(LocalTime dayEndTime) { this.dayEndTime = dayEndTime; }

    public Integer getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(Integer slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    public Integer getBreakDurationMinutes() { return breakDurationMinutes; }
    public void setBreakDurationMinutes(Integer breakDurationMinutes) { this.breakDurationMinutes = breakDurationMinutes; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public UUID getGekId() { return gekId; }
    public void setGekId(UUID gekId) { this.gekId = gekId; }

    public List<String> getLocations() { return locations; }
    public void setLocations(List<String> locations) { this.locations = locations; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
}
