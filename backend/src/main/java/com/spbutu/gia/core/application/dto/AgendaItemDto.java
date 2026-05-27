package com.spbutu.gia.core.application.dto;

import java.util.UUID;

public record AgendaItemDto(
    UUID id,
    UUID meetingId,
    UUID studentId,
    String studentFullName,
    String studentRecordBook,
    String thesisTopic,
    String supervisorName,
    Integer presentationDuration,
    Double averageScore,
    Double overallAverageScore,
    Long voteCount,
    String decision,
    String thesisFilePath,
    String thesisFileName
) {}
