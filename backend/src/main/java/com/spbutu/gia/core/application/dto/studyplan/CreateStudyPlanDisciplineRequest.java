package com.spbutu.gia.core.application.dto.studyplan;

import java.util.UUID;

public class CreateStudyPlanDisciplineRequest {
    private UUID disciplineId;
    private Integer semester;
    private Integer course;
    private Integer hours;
    private Integer credits;
    private String controlType;
    private Boolean isMandatory;

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }

    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getControlType() { return controlType; }
    public void setControlType(String controlType) { this.controlType = controlType; }

    public Boolean getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Boolean isMandatory) { this.isMandatory = isMandatory; }
}
