package com.spbutu.gia.assignments.domain;

import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;
import com.spbutu.gia.auth.domain.AppUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Задание, создаваемое преподавателем.
 */
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentType assignmentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_group_id")
    private Group targetGroup;

    @Type(JsonType.class)
    @Column(columnDefinition = "uuid[]")
    private List<UUID> targetStudentIds;

    private ZonedDateTime deadline;

    @Column(name = "allow_late_submission")
    private Boolean allowLateSubmission = false;

    @Column(name = "semester")
    private Integer semester = 1;

    @Column(name = "max_score")
    private Integer maxScore = 100;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private ScoringConfig scoringConfig;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<AttachedFile> attachedFiles;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Getters / Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AssignmentType getAssignmentType() { return assignmentType; }
    public void setAssignmentType(AssignmentType assignmentType) { this.assignmentType = assignmentType; }

    public AppUser getCreatedBy() { return createdBy; }
    public void setCreatedBy(AppUser createdBy) { this.createdBy = createdBy; }

    public Group getTargetGroup() { return targetGroup; }
    public void setTargetGroup(Group targetGroup) { this.targetGroup = targetGroup; }

    public List<UUID> getTargetStudentIds() { return targetStudentIds; }
    public void setTargetStudentIds(List<UUID> targetStudentIds) { this.targetStudentIds = targetStudentIds; }

    public ZonedDateTime getDeadline() { return deadline; }
    public void setDeadline(ZonedDateTime deadline) { this.deadline = deadline; }

    public Boolean getAllowLateSubmission() { return allowLateSubmission; }
    public void setAllowLateSubmission(Boolean allowLateSubmission) { this.allowLateSubmission = allowLateSubmission; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

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
}
