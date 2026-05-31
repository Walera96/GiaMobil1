package com.spbutu.gia.assignments.domain.entity;

import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
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
 * Сдача задания студентом.
 */
@Entity
@Table(name = "assignment_submissions")
@EntityListeners(AuditingEntityListener.class)
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "assignment_id", nullable = false)
    private UUID assignmentId;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Type(JsonType.class)
    @Column(name = "solution_files", columnDefinition = "jsonb")
    private List<AttachedFile> solutionFiles = new ArrayList<>();

    @Column(name = "student_comment", columnDefinition = "TEXT")
    private String studentComment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @Column(name = "submitted_at")
    private ZonedDateTime submittedAt;

    @Type(JsonType.class)
    @Column(name = "score", columnDefinition = "jsonb")
    private ScoreBreakdown score;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "teacher_feedback", columnDefinition = "TEXT")
    private String teacherFeedback;

    @Column(name = "teacher_comment", columnDefinition = "TEXT")
    private String teacherComment;

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private ZonedDateTime reviewedAt;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "previous_version_id")
    private UUID previousVersionId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public AssignmentSubmission() {
    }

    // --- Getters & Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(UUID assignmentId) {
        this.assignmentId = assignmentId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public List<AttachedFile> getSolutionFiles() {
        return solutionFiles;
    }

    public void setSolutionFiles(List<AttachedFile> solutionFiles) {
        this.solutionFiles = solutionFiles;
    }

    public String getStudentComment() {
        return studentComment;
    }

    public void setStudentComment(String studentComment) {
        this.studentComment = studentComment;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public ZonedDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(ZonedDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public ScoreBreakdown getScore() {
        return score;
    }

    public void setScore(ScoreBreakdown score) {
        this.score = score;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getTeacherFeedback() {
        return teacherFeedback;
    }

    public void setTeacherFeedback(String teacherFeedback) {
        this.teacherFeedback = teacherFeedback;
    }

    public String getTeacherComment() {
        return teacherComment;
    }

    public void setTeacherComment(String teacherComment) {
        this.teacherComment = teacherComment;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(UUID reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public ZonedDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(ZonedDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public UUID getPreviousVersionId() {
        return previousVersionId;
    }

    public void setPreviousVersionId(UUID previousVersionId) {
        this.previousVersionId = previousVersionId;
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

    /**
     * Вложенный VO: разбивка оценки по критериям.
     */
    public static class ScoreBreakdown {
        private Integer total;
        private java.util.List<CriterionScore> criteria;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public java.util.List<CriterionScore> getCriteria() {
            return criteria;
        }

        public void setCriteria(java.util.List<CriterionScore> criteria) {
            this.criteria = criteria;
        }

        public static class CriterionScore {
            private String criterionName;
            private Integer score;
            private String comment;

            public String getCriterionName() {
                return criterionName;
            }

            public void setCriterionName(String criterionName) {
                this.criterionName = criterionName;
            }

            public Integer getScore() {
                return score;
            }

            public void setScore(Integer score) {
                this.score = score;
            }

            public String getComment() {
                return comment;
            }

            public void setComment(String comment) {
                this.comment = comment;
            }
        }
    }
}
