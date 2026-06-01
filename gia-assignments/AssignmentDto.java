package com.spbutu.gia.assignments.dto;

import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для отображения задания (преподавателю или студенту).
 */
public class AssignmentDto {

    private UUID id;
    private String title;
    private String description;
    private AssignmentType assignmentType;
    private UUID createdById;
    private String createdByName;
    private UUID targetGroupId;
    private String targetGroupName;
    private List<UUID> targetStudentIds;
    private ZonedDateTime deadline;
    private Boolean allowLateSubmission;
    private Integer maxScore;
    private ScoringConfig scoringConfig;
    private List<AttachedFile> attachedFiles;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    // Для студента: статус его сдачи
    private SubmissionStatus myStatus;
    private BigDecimal myScore;
    private Integer myVersion;

    // Getters / Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AssignmentType getAssignmentType() { return assignmentType; }
    public void setAssignmentType(AssignmentType assignmentType) { this.assignmentType = assignmentType; }

    public UUID getCreatedById() { return createdById; }
    public void setCreatedById(UUID createdById) { this.createdById = createdById; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public UUID getTargetGroupId() { return targetGroupId; }
    public void setTargetGroupId(UUID targetGroupId) { this.targetGroupId = targetGroupId; }

    public String getTargetGroupName() { return targetGroupName; }
    public void setTargetGroupName(String targetGroupName) { this.targetGroupName = targetGroupName; }

    public List<UUID> getTargetStudentIds() { return targetStudentIds; }
    public void setTargetStudentIds(List<UUID> targetStudentIds) { this.targetStudentIds = targetStudentIds; }

    public ZonedDateTime getDeadline() { return deadline; }
    public void setDeadline(ZonedDateTime deadline) { this.deadline = deadline; }

    public Boolean getAllowLateSubmission() { return allowLateSubmission; }
    public void setAllowLateSubmission(Boolean allowLateSubmission) { this.allowLateSubmission = allowLateSubmission; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public ScoringConfig getScoringConfig() { return scoringConfig; }
    public void setScoringConfig(ScoringConfig scoringConfig) { this.scoringConfig = scoringConfig; }

    public List<AttachedFile> getAttachedFiles() { return attachedFiles; }
    public void setAttachedFiles(List<AttachedFile> attachedFiles) { this.attachedFiles = attachedFiles; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }

    public SubmissionStatus getMyStatus() { return myStatus; }
    public void setMyStatus(SubmissionStatus myStatus) { this.myStatus = myStatus; }

    public BigDecimal getMyScore() { return myScore; }
    public void setMyScore(BigDecimal myScore) { this.myScore = myScore; }

    public Integer getMyVersion() { return myVersion; }
    public void setMyVersion(Integer myVersion) { this.myVersion = myVersion; }
}
