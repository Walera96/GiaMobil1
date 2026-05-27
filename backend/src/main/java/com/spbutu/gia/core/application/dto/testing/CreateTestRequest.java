package com.spbutu.gia.core.application.dto.testing;

import java.util.List;
import java.util.UUID;

public class CreateTestRequest {
    private String title;
    private String description;
    private UUID disciplineId;
    private UUID directionId;
    private Integer durationMinutes;
    private Integer passingScore;
    private String status;
    private List<CreateQuestionRequest> questions;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

    public UUID getDirectionId() { return directionId; }
    public void setDirectionId(UUID directionId) { this.directionId = directionId; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<CreateQuestionRequest> getQuestions() { return questions; }
    public void setQuestions(List<CreateQuestionRequest> questions) { this.questions = questions; }
}
