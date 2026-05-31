package com.spbutu.gia.assignments.application.dto;

import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO задания для ответа API.
 */
public record AssignmentDto(
        UUID id,
        String title,
        String description,
        AssignmentType assignmentType,
        UUID createdBy,
        String createdByName,
        UUID targetGroupId,
        String targetGroupName,
        List<UUID> targetStudentIds,
        ZonedDateTime deadline,
        boolean allowLateSubmission,
        Integer maxScore,
        ScoringConfig scoringConfig,
        List<AttachedFile> attachedFiles,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
