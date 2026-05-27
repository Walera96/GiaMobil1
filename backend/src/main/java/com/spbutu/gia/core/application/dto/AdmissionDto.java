package com.spbutu.gia.core.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO допуска студента к аттестации.
 */
public record AdmissionDto(
        UUID id,
        UUID studentId,
        String studentFullName,
        String groupName,
        Integer brsScore,
        Boolean hasDebt,
        Boolean isAdmitted,
        LocalDateTime checkedAt,
        LocalDateTime createdAt
) {
}
