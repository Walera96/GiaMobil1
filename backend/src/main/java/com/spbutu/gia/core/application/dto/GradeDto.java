package com.spbutu.gia.core.application.dto;

import java.util.UUID;

public class GradeDto {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID disciplineId;
    private String disciplineName;
    private String subjectName;
    private Integer score;
    private Integer currentControl;
    private Integer attendance;
    private Integer activity;
    private Integer examScore;
    private Integer totalScore;
    private String ectsGrade;
    private Integer fivePointGrade;
    private String semester;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

    public String getDisciplineName() { return disciplineName; }
    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

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

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
