package com.spbutu.gia.assignments.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO для оценки сдачи преподавателем.
 */
public record ReviewDto(
        @NotNull Integer totalScore,
        List<CriterionScoreDto> criteriaScores,
        String teacherFeedback,
        String teacherComment,
        boolean returnForRevision
) {

    public record CriterionScoreDto(
            String criterionName,
            Integer score,
            String comment
    ) {
    }
}
