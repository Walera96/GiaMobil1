package com.spbutu.gia.core.application.dto;

import java.util.List;
import java.util.UUID;

public class CreateStatementRequest {
    private String statementNumber;
    private String academicYear;
    private String semester;
    private UUID groupId;
    private UUID disciplineId;
    private UUID teacherId;
    private List<StatementRecordDto> records;

    public String getStatementNumber() { return statementNumber; }
    public void setStatementNumber(String statementNumber) { this.statementNumber = statementNumber; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }

    public List<StatementRecordDto> getRecords() { return records; }
    public void setRecords(List<StatementRecordDto> records) { this.records = records; }
}
