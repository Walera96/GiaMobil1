package com.spbutu.gia.core.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO информации о заседании ГЭК для студента.
 */
public record StudentMeetingInfoDto(
        UUID meetingId,
        LocalDateTime meetingDate,
        String startTime,
        String endTime,
        String location,
        String gekName,
        Integer orderNumber
) {
}
