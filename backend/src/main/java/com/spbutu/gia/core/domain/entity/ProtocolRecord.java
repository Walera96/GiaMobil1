package com.spbutu.gia.core.domain.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запись в протоколе (итоговая оценка студента по результатам голосования).
 */
@Entity
@Table(
    name = "protocol_record",
    uniqueConstraints = @UniqueConstraint(columnNames = {"protocol_id", "student_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class ProtocolRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocol_id", nullable = false)
    private Protocol protocol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "average_score")
    private Integer averageScore;

    @Column(name = "final_score")
    private Integer finalScore;

    @Column(name = "score_points")
    private Integer scorePoints;

    @Column(name = "is_absent")
    private Boolean isAbsent = false;

    @Column(name = "qualification", length = 200)
    private String qualification;

    @Column(name = "is_with_honors")
    private Boolean isWithHonors;

    @Column(name = "decision", length = 500)
    private String decision;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ProtocolRecord() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Integer averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Integer finalScore) {
        this.finalScore = finalScore;
    }

    public Integer getScorePoints() {
        return scorePoints;
    }

    public void setScorePoints(Integer scorePoints) {
        this.scorePoints = scorePoints;
    }

    public Boolean getIsAbsent() {
        return isAbsent;
    }

    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public Boolean getIsWithHonors() {
        return isWithHonors;
    }

    public void setIsWithHonors(Boolean isWithHonors) {
        this.isWithHonors = isWithHonors;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
