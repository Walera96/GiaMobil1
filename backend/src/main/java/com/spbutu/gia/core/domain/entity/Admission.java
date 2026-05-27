package com.spbutu.gia.core.domain.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Допуск студента к государственной итоговой аттестации.
 * Проверяются: баллы БРС ≥ 60, отсутствие задолженностей.
 */
@Entity
@Table(name = "admission")
@EntityListeners(AuditingEntityListener.class)
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(name = "brs_score")
    private Integer brsScore;

    @Column(name = "has_debt", nullable = false)
    private Boolean hasDebt;

    @Column(name = "is_admitted", nullable = false)
    private Boolean isAdmitted;

    @Column(name = "checked_at")
    private LocalDateTime checkedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Admission() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getBrsScore() {
        return brsScore;
    }

    public void setBrsScore(Integer brsScore) {
        this.brsScore = brsScore;
    }

    public Boolean getHasDebt() {
        return hasDebt;
    }

    public void setHasDebt(Boolean hasDebt) {
        this.hasDebt = hasDebt;
    }

    public Boolean getIsAdmitted() {
        return isAdmitted;
    }

    public void setIsAdmitted(Boolean isAdmitted) {
        this.isAdmitted = isAdmitted;
    }

    public LocalDateTime getCheckedAt() {
        return checkedAt;
    }

    public void setCheckedAt(LocalDateTime checkedAt) {
        this.checkedAt = checkedAt;
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
