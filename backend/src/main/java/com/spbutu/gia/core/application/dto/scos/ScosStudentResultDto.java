package com.spbutu.gia.core.application.dto.scos;

public class ScosStudentResultDto {
    private String studentId;
    private String lastName;
    private String firstName;
    private String middleName;
    private String recordBookNumber;
    private String groupName;
    private String directionCode;
    private String thesisTopic;
    private String supervisorName;
    private Integer finalScore;
    private String ectsGrade;
    private Integer fivePointGrade;
    private String qualificationAwarded;
    private String protocolNumber;
    private String protocolDate;
    private String decision;

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getRecordBookNumber() { return recordBookNumber; }
    public void setRecordBookNumber(String recordBookNumber) { this.recordBookNumber = recordBookNumber; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getThesisTopic() { return thesisTopic; }
    public void setThesisTopic(String thesisTopic) { this.thesisTopic = thesisTopic; }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }

    public Integer getFinalScore() { return finalScore; }
    public void setFinalScore(Integer finalScore) { this.finalScore = finalScore; }

    public String getEctsGrade() { return ectsGrade; }
    public void setEctsGrade(String ectsGrade) { this.ectsGrade = ectsGrade; }

    public Integer getFivePointGrade() { return fivePointGrade; }
    public void setFivePointGrade(Integer fivePointGrade) { this.fivePointGrade = fivePointGrade; }

    public String getQualificationAwarded() { return qualificationAwarded; }
    public void setQualificationAwarded(String qualificationAwarded) { this.qualificationAwarded = qualificationAwarded; }

    public String getProtocolNumber() { return protocolNumber; }
    public void setProtocolNumber(String protocolNumber) { this.protocolNumber = protocolNumber; }

    public String getProtocolDate() { return protocolDate; }
    public void setProtocolDate(String protocolDate) { this.protocolDate = protocolDate; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
}
