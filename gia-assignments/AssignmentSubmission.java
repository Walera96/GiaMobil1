package com.spbutu.gia.assignments.domain;

import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.auth.domain.AppUser;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сдача задания студентом.
 */
@Entity
@Table(name = "assignment_submissions")
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private AppUser student;

    @Type(JsonType.class)
    @Column(name = "solution_files", columnDefinition = "jsonb", nullable = false)
    private List<AttachedFile> solutionFiles;

    @Column(name = "student_comment", columnDefinition = "TEXT")
    private String studentComment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @Column(name = "submitted_at")
    private ZonedDateTime submittedAt;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode score;

    @Column(name = "total_score", precision = 5, scale = 2)
    private BigDecimal totalScore;

    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;

    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;

    /** Баллы с предыдущего семестра (для накопительной системы) */
    @Column(name = "previous_semester_score", precision = 5, scale = 2)
    private BigDecimal previousSemesterScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private AppUser reviewedBy;

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;

    @Column(nullable = false)
    private Integer version = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_version_id")
    private AssignmentSubmission previousVersion;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Getters / Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public AppUser getStudent() { return student; }
    public void setStudent(AppUser student) { this.student = student; }

    public List<AttachedFile> getSolutionFiles() { return solutionFiles; }
    public void setSolutionFiles(List<AttachedFile> solutionFiles) { this.solutionFiles = solutionFiles; }

    public String getStudentComment() { return studentComment; }
    public void setStudentComment(String studentComment) { this.studentComment = studentComment; }

    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }

    public ZonedDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(ZonedDateTime submittedAt) { this.submittedAt = submittedAt; }

    public JsonNode getScore() { return score; }
    public void setScore(JsonNode score) { this.score = score; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public String getTeacherFeedback() { return teacherFeedback; }
    public void setTeacherFeedback(String teacherFeedback) { this.teacherFeedback = teacherFeedback; }

    public String getTeacherComment() { return teacherComment; }
    public void setTeacherComment(String teacherComment) { this.teacherComment = teacherComment; }

    public BigDecimal getPreviousSemesterScore() { return previousSemesterScore; }
    public void setPreviousSemesterScore(BigDecimal previousSemesterScore) { this.previousSemesterScore = previousSemesterScore; }

    public AppUser getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(AppUser reviewedBy) { this.reviewedBy = reviewedBy; }

    public ZonedDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(ZonedDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public AssignmentSubmission getPreviousVersion() { return previousVersion; }
    public void setPreviousVersion(AssignmentSubmission previousVersion) { this.previousVersion = previousVersion; }

    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }

    public ZonedDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(ZonedDateTime updatedAt) { this.updatedAt = updatedAt; }
}
