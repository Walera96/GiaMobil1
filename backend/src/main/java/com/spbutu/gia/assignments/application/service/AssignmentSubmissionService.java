package com.spbutu.gia.assignments.application.service;

import com.spbutu.gia.assignments.application.dto.ReviewDto;
import com.spbutu.gia.assignments.application.dto.SubmissionDto;
import com.spbutu.gia.assignments.domain.entity.AssignmentSubmission;
import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.domain.repository.AssignmentSubmissionRepository;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;

    public AssignmentSubmissionService(AssignmentSubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public SubmissionDto submit(UUID assignmentId, UUID studentId,
                                 List<AttachedFile> solutionFiles, String studentComment) {
        // Закрываем предыдущую версию
        var previous = submissionRepository.findTopByAssignmentIdAndStudentIdOrderByVersionDesc(assignmentId, studentId);

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignmentId(assignmentId);
        submission.setStudentId(studentId);
        submission.setSolutionFiles(solutionFiles);
        submission.setStudentComment(studentComment);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(ZonedDateTime.now());
        submission.setVersion(previous.map(p -> p.getVersion() + 1).orElse(1));
        previous.ifPresent(p -> submission.setPreviousVersionId(p.getId()));

        AssignmentSubmission saved = submissionRepository.save(submission);
        return toDto(saved);
    }

    @Transactional
    public SubmissionDto review(UUID submissionId, ReviewDto dto, UUID teacherId) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Сдача не найдена"));

        submission.setTotalScore(dto.totalScore());
        submission.setScore(toScoreBreakdown(dto));
        submission.setTeacherFeedback(dto.teacherFeedback());
        submission.setTeacherComment(dto.teacherComment());
        submission.setReviewedBy(teacherId);
        submission.setReviewedAt(ZonedDateTime.now());
        submission.setStatus(dto.returnForRevision() ? SubmissionStatus.RETURNED : SubmissionStatus.REVIEWED);

        AssignmentSubmission saved = submissionRepository.save(submission);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SubmissionDto> findByAssignment(UUID assignmentId) {
        return submissionRepository.findAllByAssignmentId(assignmentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubmissionDto> findByStudent(UUID studentId) {
        return submissionRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubmissionDto findById(UUID id) {
        AssignmentSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сдача не найдена"));
        return toDto(submission);
    }

    private AssignmentSubmission.ScoreBreakdown toScoreBreakdown(ReviewDto dto) {
        if (dto.criteriaScores() == null || dto.criteriaScores().isEmpty()) {
            return null;
        }
        var breakdown = new AssignmentSubmission.ScoreBreakdown();
        breakdown.setTotal(dto.totalScore());
        breakdown.setCriteria(dto.criteriaScores().stream()
                .map(cs -> {
                    var c = new AssignmentSubmission.ScoreBreakdown.CriterionScore();
                    c.setCriterionName(cs.criterionName());
                    c.setScore(cs.score());
                    c.setComment(cs.comment());
                    return c;
                })
                .collect(Collectors.toList()));
        return breakdown;
    }

    private SubmissionDto toDto(AssignmentSubmission s) {
        return new SubmissionDto(
                s.getId(),
                s.getAssignmentId(),
                null, // assignmentTitle
                s.getStudentId(),
                null, // studentName
                s.getSolutionFiles(),
                s.getStudentComment(),
                s.getStatus(),
                s.getSubmittedAt(),
                s.getTotalScore(),
                s.getTeacherFeedback(),
                s.getTeacherComment(),
                s.getReviewedBy(),
                null, // reviewedByName
                s.getReviewedAt(),
                s.getVersion(),
                s.getPreviousVersionId(),
                s.getCreatedAt(),
                s.getUpdatedAt()
        );
    }
}
