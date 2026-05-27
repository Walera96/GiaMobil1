package com.spbutu.gia.core.application.dto;

import java.util.UUID;

public class CreateGradeRequest {
    private UUID studentId;
    private UUID disciplineId;
    private String subjectName;
    private Integer score;
    private Integer currentControl;
    private Integer attendance;
    private Integer activity;
    private Integer examScore;
    private String semester;

    public UUID getStudentId() { return studentId; }
    public void setStudentId(UUID studentId) { this.studentId = studentId; }

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

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

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
}
