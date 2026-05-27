package com.spbutu.gia.core.domain.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Пункт повестки заседания (студент, выступающий на ГЭК).
 */
@Entity
@Table(
    name = "agenda_item",
    uniqueConstraints = @UniqueConstraint(columnNames = {"meeting_id", "student_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class AgendaItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_id", nullable = false)
    private Meeting meeting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;

    @Column(name = "presentation_duration")
    private Integer presentationDuration = 10;

    @Column(name = "presentation_materials", length = 300)
    private String presentationMaterials;

    @Column(name = "average_score", columnDefinition = "NUMERIC(4,2)")
    private Double averageScore;

    @Column(name = "overall_average_score", columnDefinition = "NUMERIC(4,2)")
    private Double overallAverageScore;

    @Column(name = "scheduled_time")
    private LocalDateTime scheduledTime;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AgendaItem() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getPresentationDuration() {
        return presentationDuration;
    }

    public void setPresentationDuration(Integer presentationDuration) {
        this.presentationDuration = presentationDuration;
    }

    public String getPresentationMaterials() {
        return presentationMaterials;
    }

    public void setPresentationMaterials(String presentationMaterials) {
        this.presentationMaterials = presentationMaterials;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public Double getOverallAverageScore() {
        return overallAverageScore;
    }

    public void setOverallAverageScore(Double overallAverageScore) {
        this.overallAverageScore = overallAverageScore;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
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
