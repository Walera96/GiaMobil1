package com.spbutu.gia.assignments.domain.entity;

import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Задание, созданное преподавателем.
 */
@Entity
@Table(name = "assignments")
@EntityListeners(AuditingEntityListener.class)
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_type", nullable = false)
    private AssignmentType assignmentType;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "target_group_id")
    private UUID targetGroupId;

    @Column(name = "target_student_ids")
    @Type(JsonType.class)
    private List<UUID> targetStudentIds = new ArrayList<>();

    @Column(name = "deadline")
    private ZonedDateTime deadline;

    @Column(name = "allow_late_submission", nullable = false)
    private boolean allowLateSubmission = false;

    @Column(name = "max_score")
    private Integer maxScore;

    @Type(JsonType.class)
    @Column(name = "scoring_config", columnDefinition = "jsonb")
    private ScoringConfig scoringConfig;

    @Type(JsonType.class)
    @Column(name = "attached_files", columnDefinition = "jsonb")
    private List<AttachedFile> attachedFiles = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public Assignment() {
    }

    // --- Getters & Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AssignmentType getAssignmentType() {
        return assignmentType;
    }

    public void setAssignmentType(AssignmentType assignmentType) {
        this.assignmentType = assignmentType;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getTargetGroupId() {
        return targetGroupId;
    }

    public void setTargetGroupId(UUID targetGroupId) {
        this.targetGroupId = targetGroupId;
    }

    public List<UUID> getTargetStudentIds() {
        return targetStudentIds;
    }

    public void setTargetStudentIds(List<UUID> targetStudentIds) {
        this.targetStudentIds = targetStudentIds;
    }

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(ZonedDateTime deadline) {
        this.deadline = deadline;
    }

    public boolean isAllowLateSubmission() {
        return allowLateSubmission;
    }

    public void setAllowLateSubmission(boolean allowLateSubmission) {
        this.allowLateSubmission = allowLateSubmission;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public ScoringConfig getScoringConfig() {
        return scoringConfig;
    }

    public void setScoringConfig(ScoringConfig scoringConfig) {
        this.scoringConfig = scoringConfig;
    }

    public List<AttachedFile> getAttachedFiles() {
        return attachedFiles;
    }

    public void setAttachedFiles(List<AttachedFile> attachedFiles) {
        this.attachedFiles = attachedFiles;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
