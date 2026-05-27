package com.spbutu.gia.core.application.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для генерации ведомости защиты ВКР.
 */
public class VedomostDto {

    private String documentNumber;
    private String academicYear;
    private String directionCode;
    private String directionName;
    private String directionShort;
    private String department;
    private String giaForm;
    private String chairmanName;
    private String chairmanDegree;
    private List<CommitteeMember> committeeMembers = new ArrayList<>();
    private String groupName;
    private Integer course;
    private LocalDate date;
    private List<StudentRecord> students = new ArrayList<>();
    private String directorName;
    private String instituteName = "Институт управления и информационных технологий";

    // Статистика (автоматический подсчет)
    private Integer totalStudents;
    private Integer countZachteno;
    private Integer countNeZachteno;
    private Integer countOtlichno;
    private Integer countHorosho;
    private Integer countUdov;
    private Integer countNeud;
    private Integer countAbsent;

    public void calculateStatistics() {
        if (students == null) {
            this.totalStudents = 0;
            this.countZachteno = 0;
            this.countNeZachteno = 0;
            this.countOtlichno = 0;
            this.countHorosho = 0;
            this.countUdov = 0;
            this.countNeud = 0;
            this.countAbsent = 0;
            return;
        }
        this.totalStudents = students.size();
        this.countZachteno = (int) students.stream().filter(s -> "зачтено".equals(s.getScoreClassic())).count();
        this.countNeZachteno = (int) students.stream().filter(s -> "не зачтено".equals(s.getScoreClassic())).count();
        this.countOtlichno = (int) students.stream().filter(s -> "отлично".equals(s.getScoreClassic())).count();
        this.countHorosho = (int) students.stream().filter(s -> "хорошо".equals(s.getScoreClassic())).count();
        this.countUdov = (int) students.stream().filter(s -> "удовлетворительно".equals(s.getScoreClassic())).count();
        this.countNeud = (int) students.stream().filter(s -> "неудовлетворительно".equals(s.getScoreClassic())).count();
        this.countAbsent = (int) students.stream().filter(s -> s.getScoreClassic() == null || s.getScoreClassic().isEmpty()).count();
    }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getDirectionName() { return directionName; }
    public void setDirectionName(String directionName) { this.directionName = directionName; }

    public String getDirectionShort() { return directionShort; }
    public void setDirectionShort(String directionShort) { this.directionShort = directionShort; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getGiaForm() { return giaForm; }
    public void setGiaForm(String giaForm) { this.giaForm = giaForm; }

    public String getChairmanName() { return chairmanName; }
    public void setChairmanName(String chairmanName) { this.chairmanName = chairmanName; }

    public String getChairmanDegree() { return chairmanDegree; }
    public void setChairmanDegree(String chairmanDegree) { this.chairmanDegree = chairmanDegree; }

    public List<CommitteeMember> getCommitteeMembers() { return committeeMembers; }
    public void setCommitteeMembers(List<CommitteeMember> committeeMembers) { this.committeeMembers = committeeMembers != null ? committeeMembers : new ArrayList<>(); }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getCourse() { return course; }
    public void setCourse(Integer course) { this.course = course; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<StudentRecord> getStudents() { return students; }
    public void setStudents(List<StudentRecord> students) { this.students = students != null ? students : new ArrayList<>(); }

    public String getDirectorName() { return directorName; }
    public void setDirectorName(String directorName) { this.directorName = directorName; }

    public String getInstituteName() { return instituteName; }
    public void setInstituteName(String instituteName) { this.instituteName = instituteName; }

    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }

    public Integer getCountZachteno() { return countZachteno; }
    public void setCountZachteno(Integer countZachteno) { this.countZachteno = countZachteno; }

    public Integer getCountNeZachteno() { return countNeZachteno; }
    public void setCountNeZachteno(Integer countNeZachteno) { this.countNeZachteno = countNeZachteno; }

    public Integer getCountOtlichno() { return countOtlichno; }
    public void setCountOtlichno(Integer countOtlichno) { this.countOtlichno = countOtlichno; }

    public Integer getCountHorosho() { return countHorosho; }
    public void setCountHorosho(Integer countHorosho) { this.countHorosho = countHorosho; }

    public Integer getCountUdov() { return countUdov; }
    public void setCountUdov(Integer countUdov) { this.countUdov = countUdov; }

    public Integer getCountNeud() { return countNeud; }
    public void setCountNeud(Integer countNeud) { this.countNeud = countNeud; }

    public Integer getCountAbsent() { return countAbsent; }
    public void setCountAbsent(Integer countAbsent) { this.countAbsent = countAbsent; }

    public static class CommitteeMember {
        private String fullName;
        private String degree;
        private String position;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getDegree() { return degree; }
        public void setDegree(String degree) { this.degree = degree; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
    }

    public static class StudentRecord {
        private Integer seqNumber;
        private String fullName;
        private String recordBookNumber;
        private Integer scorePoints;
        private String scoreClassic;

        public Integer getSeqNumber() { return seqNumber; }
        public void setSeqNumber(Integer seqNumber) { this.seqNumber = seqNumber; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getRecordBookNumber() { return recordBookNumber; }
        public void setRecordBookNumber(String recordBookNumber) { this.recordBookNumber = recordBookNumber; }

        public Integer getScorePoints() { return scorePoints; }
        public void setScorePoints(Integer scorePoints) { this.scorePoints = scorePoints; }

        public String getScoreClassic() { return scoreClassic; }
        public void setScoreClassic(String scoreClassic) { this.scoreClassic = scoreClassic; }
    }
}
