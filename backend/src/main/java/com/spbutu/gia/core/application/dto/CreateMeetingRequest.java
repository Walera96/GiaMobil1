package com.spbutu.gia.core.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Запрос на создание заседания ГЭК.
 */
public record CreateMeetingRequest(
        @NotNull(message = "ID ГЭК обязательно")
        UUID gekId,

        @NotNull(message = "Дата заседания обязательна")
        LocalDateTime meetingDate,

        String location,

        @Min(value = 2, message = "Кворум не может быть меньше 2")
        Integer quorumRequired
) {
}
