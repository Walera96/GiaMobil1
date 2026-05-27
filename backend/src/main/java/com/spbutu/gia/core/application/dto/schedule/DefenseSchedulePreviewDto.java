package com.spbutu.gia.core.application.dto.schedule;

import java.util.List;

public class DefenseSchedulePreviewDto {
    private String groupName;
    private String directionName;
    private Integer totalStudents;
    private Integer totalDays;
    private List<DefenseDayDto> days;
    private List<String> warnings;

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDirectionName() { return directionName; }
    public void setDirectionName(String directionName) { this.directionName = directionName; }

    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }

    public Integer getTotalDays() { return totalDays; }
    public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }

    public List<DefenseDayDto> getDays() { return days; }
    public void setDays(List<DefenseDayDto> days) { this.days = days; }

    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
}
