package com.spbutu.gia.core.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO записи протокола (итоговой оценки студента).
 */
public record ProtocolRecordDto(
        UUID id,
        UUID protocolId,
        UUID studentId,
        String studentFullName,
        String recordBookNumber,
        Integer scorePoints,
        Integer finalScore,
        Boolean isAbsent,
        String qualification,
        Boolean isWithHonors,
        String decision,
        String groupName,
        String directionCode,
        String directionName,
        LocalDateTime createdAt
) {
}
