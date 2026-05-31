package com.spbutu.gia.assignments.application.dto;

import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для создания задания.
 */
public record AssignmentCreateDto(
        @NotBlank String title,
        String description,
        @NotNull AssignmentType assignmentType,
        UUID targetGroupId,
        List<UUID> targetStudentIds,
        ZonedDateTime deadline,
        boolean allowLateSubmission,
        Integer maxScore,
        ScoringConfig scoringConfig,
        List<AttachedFile> attachedFiles
) {
}
