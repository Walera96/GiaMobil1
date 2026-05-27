package com.spbutu.gia.core.application.dto;

public record ScoreSheetRowDto(
    Integer number,
    String studentFullName,
    String recordBookNumber,
    Integer scorePoints,
    Integer finalScore,
    String result
) {}
