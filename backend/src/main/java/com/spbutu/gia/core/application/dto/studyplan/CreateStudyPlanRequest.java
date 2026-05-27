package com.spbutu.gia.core.application.dto.studyplan;

import java.util.List;
import java.util.UUID;

public class CreateStudyPlanRequest {
    private String name;
    private UUID directionId;
    private String profile;
    private String academicYear;
    private String formOfStudy;
    private String qualification;
    private Integer totalHours;
    private Integer totalCredits;
    private String status;
    private List<CreateStudyPlanDisciplineRequest> disciplines;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getDirectionId() { return directionId; }
    public void setDirectionId(UUID directionId) { this.directionId = directionId; }

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

    public List<CreateStudyPlanDisciplineRequest> getDisciplines() { return disciplines; }
    public void setDisciplines(List<CreateStudyPlanDisciplineRequest> disciplines) { this.disciplines = disciplines; }
}
