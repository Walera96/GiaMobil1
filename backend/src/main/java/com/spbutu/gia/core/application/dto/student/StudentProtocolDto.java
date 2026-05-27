package com.spbutu.gia.core.application.dto.student;

import java.util.UUID;

public class StudentProtocolDto {
    private UUID id;
    private Integer finalScore;
    private String decision;
    private String protocolNumber;
    private String protocolDate;
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getFinalScore() { return finalScore; }
    public void setFinalScore(Integer finalScore) { this.finalScore = finalScore; }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }

    public String getProtocolNumber() { return protocolNumber; }
    public void setProtocolNumber(String protocolNumber) { this.protocolNumber = protocolNumber; }

    public String getProtocolDate() { return protocolDate; }
    public void setProtocolDate(String protocolDate) { this.protocolDate = protocolDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
