package com.spbutu.gia.core.domain.entity.studyplan;

import com.spbutu.gia.core.domain.entity.Direction;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "study_plan")
public class StudyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direction_id", nullable = false)
    private Direction direction;

    @Column(length = 200)
    private String profile;

    @Column(name = "academic_year", length = 20)
    private String academicYear;

    @Column(name = "form_of_study", length = 20)
    private String formOfStudy; // ОЧНАЯ, ЗАОЧНАЯ, ОЧНО_ЗАОЧНАЯ

    @Column(length = 50)
    private String qualification; // БАКАЛАВР, МАГИСТР, СПЕЦИАЛИТЕТ

    @Column(name = "total_hours")
    private Integer totalHours;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @Column(length = 20)
    private String status; // ACTIVE, ARCHIVED

    @OneToMany(mappedBy = "studyPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyPlanDiscipline> disciplines = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getFormOfStudy() { return formOfStudy; }
    public void setFormOfStudy(String formOfStudy) { this.formOfStudy = formOfStudy; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Integer getTotalHours() { return totalHours; }
    public void setTotalHours(Integer totalHours) { this.totalHours = totalHours; }

    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<StudyPlanDiscipline> getDisciplines() { return disciplines; }
    public void setDisciplines(List<StudyPlanDiscipline> disciplines) { this.disciplines = disciplines; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
