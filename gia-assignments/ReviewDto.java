package com.spbutu.gia.assignments.dto;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO для оценки сдачи преподавателем.
 */
public class ReviewDto {

    /** Гибкая оценка по критериям (JSONB) */
    private JsonNode score;

    /** Итоговый балл (число) — опционально, если score содержит структуру */
    private BigDecimal totalScore;

    /** Развёрнутый отзыв */
    private String teacherFeedback;

    /** Короткий комментарий */
    private String teacherComment;

    /** Вернуть на доработку вместо оценки */
    private Boolean returnForRevision = false;

    // Getters / Setters
    public JsonNode getScore() { return score; }
    public void setScore(JsonNode score) { this.score = score; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public String getTeacherFeedback() { return teacherFeedback; }
    public void setTeacherFeedback(String teacherFeedback) { this.teacherFeedback = teacherFeedback; }

    public String getTeacherComment() { return teacherComment; }
    public void setTeacherComment(String teacherComment) { this.teacherComment = teacherComment; }

    public Boolean getReturnForRevision() { return returnForRevision; }
    public void setReturnForRevision(Boolean returnForRevision) { this.returnForRevision = returnForRevision; }
}
