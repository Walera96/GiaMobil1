package com.spbutu.gia.assignments.application.dto;

import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO сдачи задания.
 */
public record SubmissionDto(
        UUID id,
        UUID assignmentId,
        String assignmentTitle,
        UUID studentId,
        String studentName,
        List<AttachedFile> solutionFiles,
        String studentComment,
        SubmissionStatus status,
        ZonedDateTime submittedAt,
        Integer totalScore,
        String teacherFeedback,
        String teacherComment,
        UUID reviewedBy,
        String reviewedByName,
        ZonedDateTime reviewedAt,
        Integer version,
        UUID previousVersionId,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) {
}
