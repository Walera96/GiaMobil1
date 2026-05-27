package com.spbutu.gia.core.application.dto;

import com.spbutu.gia.core.domain.enums.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO записи журнала аудита.
 */
public record AuditLogDto(
        UUID id,
        String tableName,
        UUID recordId,
        AuditAction action,
        String oldValue,
        String newValue,
        UUID changedById,
        String ipAddress,
        LocalDateTime createdAt
) {
}
