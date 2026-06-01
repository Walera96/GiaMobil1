package com.spbutu.gia.assignments.dto;

import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для создания задания преподавателем.
 */
public class AssignmentCreateDto {

    @NotBlank(message = "Название задания обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String title;

    private String description;

    @NotNull(message = "Тип задания обязателен")
    private AssignmentType assignmentType;

    /** ID группы для назначения (опционально, если targetStudentIds не указаны) */
    private UUID targetGroupId;

    /** ID конкретных студентов для индивидуального назначения */
    private List<UUID> targetStudentIds;

    private ZonedDateTime deadline;

    private Boolean allowLateSubmission = false;

    private Integer maxScore = 100;

    private ScoringConfig scoringConfig;

    private List<AttachedFile> attachedFiles;

    // Getters / Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AssignmentType getAssignmentType() { return assignmentType; }
    public void setAssignmentType(AssignmentType assignmentType) { this.assignmentType = assignmentType; }

    public UUID getTargetGroupId() { return targetGroupId; }
    public void setTargetGroupId(UUID targetGroupId) { this.targetGroupId = targetGroupId; }

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
}
