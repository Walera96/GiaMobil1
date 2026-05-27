package com.spbutu.gia.core.application.dto.testing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TestDto {
    private UUID id;
    private String title;
    private String description;
    private UUID disciplineId;
    private String disciplineName;
    private UUID directionId;
    private String directionName;
    private Integer durationMinutes;
    private Integer passingScore;
    private Integer maxScore;
    private String status;
    private Integer questionCount;
    private List<QuestionDto> questions;
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public UUID getDisciplineId() { return disciplineId; }
    public void setDisciplineId(UUID disciplineId) { this.disciplineId = disciplineId; }

    public String getDisciplineName() { return disciplineName; }
    public void setDisciplineName(String disciplineName) { this.disciplineName = disciplineName; }

    public UUID getDirectionId() { return directionId; }
    public void setDirectionId(UUID directionId) { this.directionId = directionId; }

    public String getDirectionName() { return directionName; }
    public void setDirectionName(String directionName) { this.directionName = directionName; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getQuestionCount() { return questionCount; }
    public void setQuestionCount(Integer questionCount) { this.questionCount = questionCount; }

    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
