package com.spbutu.gia.core.application.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatementDto {
    private UUID id;
    private String statementNumber;
    private String academicYear;
    private String semester;
    private UUID groupId;
    private String groupName;
    private UUID disciplineId;
    private String disciplineName;
    private UUID teacherId;
    private String teacherName;
    private String status;
    private List<StatementRecordDto> records = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getStatementNumber() { return statementNumber; }
    public void setStatementNumber(String statementNumber) { this.statementNumber = statementNumber; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public UUID getGroupId() { return groupId; }
    public void setGroupId(UUID groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

    public String getDisciplineName() { return disciplineName; }
    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }

    public UUID getTeacherId() { return teacherId; }
    public void setTeacherId(UUID teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<StatementRecordDto> getRecords() { return records; }
    public void setRecords(List<StatementRecordDto> records) { this.records = records; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
