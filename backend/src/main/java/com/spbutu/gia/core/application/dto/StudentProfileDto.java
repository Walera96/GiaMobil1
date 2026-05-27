package com.spbutu.gia.core.application.dto;

import java.util.UUID;

/**
 * DTO профиля студента (личный кабинет).
 */
public record StudentProfileDto(
        UUID id,
        String fullName,
        String recordBookNumber,
        String groupName,
        String directionCode,
        String directionName,
        String thesisTopic,
        String supervisorName,
        String thesisFilePath,
        String thesisFileName,
        Double averageGrade
) {
}
