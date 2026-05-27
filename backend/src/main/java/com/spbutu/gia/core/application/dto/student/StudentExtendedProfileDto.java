package com.spbutu.gia.core.application.dto.student;

import java.util.UUID;

public class StudentExtendedProfileDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String recordBookNumber;
    private String groupName;
    private String directionCode;
    private String directionName;
    private String course;
    private String thesisTopic;
    private String supervisorName;
    private String thesisFilePath;
    private String thesisFileName;
    private String photoUrl;
    private String aboutMe;
    private Double averageGrade;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRecordBookNumber() { return recordBookNumber; }
    public void setRecordBookNumber(String recordBookNumber) { this.recordBookNumber = recordBookNumber; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getDirectionName() { return directionName; }
    public void setDirectionName(String directionName) { this.directionName = directionName; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public String getThesisFilePath() { return thesisFilePath; }
    public void setThesisFilePath(String thesisFilePath) { this.thesisFilePath = thesisFilePath; }

    public String getThesisFileName() { return thesisFileName; }
    public void setThesisFileName(String thesisFileName) { this.thesisFileName = thesisFileName; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getAboutMe() { return aboutMe; }
    public void setAboutMe(String aboutMe) { this.aboutMe = aboutMe; }

    public Double getAverageGrade() { return averageGrade; }
    public void setAverageGrade(Double averageGrade) { this.averageGrade = averageGrade; }
}
