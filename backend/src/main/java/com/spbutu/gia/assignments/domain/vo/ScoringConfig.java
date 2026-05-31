package com.spbutu.gia.assignments.domain.vo;

/**
 * Value Object: конфигурация оценивания задания.
 */
public record ScoringConfig(
        Integer maxScore,
        Integer passingScore,
        java.util.List<ScoringCriterion> criteria
) {

    /**
     * Отдельный критерий оценивания.
     */
    public record ScoringCriterion(
            String name,
            Integer weight,
            String description
    ) {
    }
}
