package com.spbutu.gia.core.application.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record ActiveMeetingDto(
        UUID id,
        String name,
        LocalDateTime meetingDate,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        List<AgendaItemDto> agendaItems
) {
}
