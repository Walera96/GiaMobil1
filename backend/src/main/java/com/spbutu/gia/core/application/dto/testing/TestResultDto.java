package com.spbutu.gia.core.application.dto.testing;

import java.util.List;
import java.util.UUID;

public class TestResultDto {
    private UUID attemptId;
    private UUID testId;
    private String testTitle;
    private Integer score;
    private Integer maxScore;
    private Integer passingScore;
    private Boolean passed;
    private Integer totalCorrect;
    private Integer totalQuestions;
    private String status;
    private List<AnswerResultDto> answerResults;

    public UUID getAttemptId() { return attemptId; }
    public void setAttemptId(UUID attemptId) { this.attemptId = attemptId; }

    public UUID getTestId() { return testId; }
    public void setTestId(UUID testId) { this.testId = testId; }

    public String getTestTitle() { return testTitle; }
    public void setTestTitle(String testTitle) { this.testTitle = testTitle; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    public Boolean getPassed() { return passed; }
    public void setPassed(Boolean passed) { this.passed = passed; }

    public Integer getTotalCorrect() { return totalCorrect; }
    public void setTotalCorrect(Integer totalCorrect) { this.totalCorrect = totalCorrect; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<AnswerResultDto> getAnswerResults() { return answerResults; }
    public void setAnswerResults(List<AnswerResultDto> answerResults) { this.answerResults = answerResults; }
}
