package com.spbutu.gia.core.application.dto;

import java.util.List;
import java.util.UUID;

public record ScoreSheetDto(
    UUID meetingId,
    String meetingTitle,
    String directionCode,
    String directionName,
    String groupName,
    List<ScoreSheetRowDto> rows,
    ScoreSheetStatsDto stats
) {}
