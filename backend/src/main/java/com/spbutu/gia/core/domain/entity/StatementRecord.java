package com.spbutu.gia.core.domain.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "statement_record")
@EntityListeners(AuditingEntityListener.class)
public class StatementRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statement_id", nullable = false)
    private Statement statement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "current_control")
    private Integer currentControl;

    private Integer attendance;

    private Integer activity;

    @Column(name = "exam_score")
    private Integer examScore;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "ects_grade", length = 2)
    private String ectsGrade;

    @Column(name = "five_point_grade")
    private Integer fivePointGrade;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StatementRecord() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Statement getStatement() { return statement; }
    public void setStatement(Statement statement) { this.statement = statement; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Integer getCurrentControl() { return currentControl; }
    public void setCurrentControl(Integer currentControl) { this.currentControl = currentControl; }

    public Integer getAttendance() { return attendance; }
    public void setAttendance(Integer attendance) { this.attendance = attendance; }

    public Integer getActivity() { return activity; }
    public void setActivity(Integer activity) { this.activity = activity; }

    public Integer getExamScore() { return examScore; }
    public void setExamScore(Integer examScore) { this.examScore = examScore; }

    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }

    public String getEctsGrade() { return ectsGrade; }
    public void setEctsGrade(String ectsGrade) { this.ectsGrade = ectsGrade; }

    public Integer getFivePointGrade() { return fivePointGrade; }
    public void setFivePointGrade(Integer fivePointGrade) { this.fivePointGrade = fivePointGrade; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
