package com.spbutu.gia.core.application.dto;

import com.spbutu.gia.core.domain.enums.ScoreValue;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Запрос на голосование члена ГЭК.
 */
public record VoteRequest(
        @NotNull(message = "ID пункта повестки обязательно")
        UUID agendaItemId,

        @NotNull(message = "Оценка обязательна")
        ScoreValue score,

        String comment
) {
}
