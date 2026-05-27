package com.spbutu.gia.core.application.dto;

import java.util.UUID;

public class DisciplineDto {
    private UUID id;
    private String code;
    private String name;
    private Integer hours;
    private Integer ectsCredits;
    private Integer course;
    private String semester;
    private String controlType;
    private UUID directionId;
    private String directionName;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }

    public Integer getEctsCredits() { return ectsCredits; }
    public void setEctsCredits(Integer ectsCredits) { this.ectsCredits = ectsCredits; }

    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getControlType() { return controlType; }
    public void setControlType(String controlType) { this.controlType = controlType; }

    public UUID getDirectionId() { return directionId; }
    public void setDirectionId(UUID directionId) { this.directionId = directionId; }

    public String getDirectionName() { return directionName; }
    public void setDirectionName(String directionName) { this.directionName = directionName; }
}
