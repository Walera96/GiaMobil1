package com.spbutu.gia.core.application.dto;

import java.util.UUID;

/**
 * DTO оценки студента по предмету.
 */
public record StudentGradeDto(
        UUID id,
        String subjectName,
        Integer score,
        String semester
) {
}
