package com.spbutu.gia.core.application.dto;

import com.spbutu.gia.core.domain.enums.MeetingStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO заседания ГЭК.
 */
public record MeetingDto(
        UUID id,
        UUID gekId,
        LocalDateTime meetingDate,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        MeetingStatus status,
        Integer quorumRequired,
        UUID createdById,
        LocalDateTime createdAt
) {
}
