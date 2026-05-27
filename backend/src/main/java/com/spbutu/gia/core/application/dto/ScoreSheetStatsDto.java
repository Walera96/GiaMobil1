package com.spbutu.gia.core.application.dto;

public record ScoreSheetStatsDto(
    Long totalStudents,
    Long presentCount,
    Long absentCount,
    Long excellentCount,
    Long goodCount,
    Long satisfactoryCount,
    Long unsatisfactoryCount,
    Long passedCount,
    Long failedCount
) {}
