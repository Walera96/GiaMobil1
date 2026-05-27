package com.spbutu.gia.core.application.dto.student;

import com.spbutu.gia.core.application.dto.AdmissionDto;
import com.spbutu.gia.core.application.dto.StudentGradeDto;
import com.spbutu.gia.core.application.dto.StudentMeetingInfoDto;

import java.util.List;

public class StudentDashboardDto {
    private StudentExtendedProfileDto profile;
    private AdmissionDto admission;
    private StudentMeetingInfoDto meetingInfo;
    private List<StudentGradeDto> grades;
    private List<StudentNotificationDto> notifications;
    private Integer unreadNotificationsCount;
    private String defenseStatus; // PENDING / SCHEDULED / COMPLETED / NOT_DEFENDED
    private String diplomaStatus; // NOT_ISSUED / ISSUED
    private Double overallProgress; // 0-100

    public StudentExtendedProfileDto getProfile() { return profile; }
    public void setProfile(StudentExtendedProfileDto profile) { this.profile = profile; }

    public AdmissionDto getAdmission() { return admission; }
    public void setAdmission(AdmissionDto admission) { this.admission = admission; }

    public StudentMeetingInfoDto getMeetingInfo() { return meetingInfo; }
    public void setMeetingInfo(StudentMeetingInfoDto meetingInfo) { this.meetingInfo = meetingInfo; }

    public List<StudentGradeDto> getGrades() { return grades; }
    public void setGrades(List<StudentGradeDto> grades) { this.grades = grades; }

    public List<StudentNotificationDto> getNotifications() { return notifications; }
    public void setNotifications(List<StudentNotificationDto> notifications) { this.notifications = notifications; }

    public Integer getUnreadNotificationsCount() { return unreadNotificationsCount; }
    public void setUnreadNotificationsCount(Integer unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; }

    public String getDefenseStatus() { return defenseStatus; }
    public void setDefenseStatus(String defenseStatus) { this.defenseStatus = defenseStatus; }

    public String getDiplomaStatus() { return diplomaStatus; }
    public void setDiplomaStatus(String diplomaStatus) { this.diplomaStatus = diplomaStatus; }

    public Double getOverallProgress() { return overallProgress; }
    public void setOverallProgress(Double overallProgress) { this.overallProgress = overallProgress; }
}
