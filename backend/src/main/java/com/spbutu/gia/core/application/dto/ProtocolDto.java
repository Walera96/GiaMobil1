package com.spbutu.gia.core.application.dto;

import com.spbutu.gia.core.domain.enums.ProtocolStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO протокола заседания.
 */
public record ProtocolDto(
        UUID id,
        UUID meetingId,
        String protocolNumber,
        ProtocolStatus status,
        LocalDateTime generatedAt,
        String filePath,
        LocalDateTime createdAt
) {
}
