package com.spbutu.gia.core.application.dto;

import com.spbutu.gia.core.domain.enums.ScoreValue;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO голоса члена ГЭК.
 */
public record VoteDto(
        UUID id,
        UUID agendaItemId,
        UUID gekMemberId,
        String gekMemberName,
        ScoreValue score,
        String comment,
        LocalDateTime votedAt
) {
}
