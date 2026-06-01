package com.spbutu.gia.assignments.service;

import com.spbutu.gia.assignments.domain.Assignment;
import com.spbutu.gia.assignments.domain.AssignmentSubmission;
import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.dto.*;
import com.spbutu.gia.assignments.repository.AssignmentRepository;
import com.spbutu.gia.assignments.repository.AssignmentSubmissionRepository;
import com.spbutu.gia.auth.domain.AppUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             AssignmentSubmissionRepository submissionRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
    }

    /** Создать задание */
    public Assignment createAssignment(AssignmentCreateDto dto, AppUser teacher) {
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setAssignmentType(dto.getAssignmentType());
        assignment.setCreatedBy(teacher);
        assignment.setDeadline(dto.getDeadline());
        assignment.setAllowLateSubmission(dto.getAllowLateSubmission());
        assignment.setMaxScore(dto.getMaxScore());
        assignment.setScoringConfig(dto.getScoringConfig());
        assignment.setAttachedFiles(dto.getAttachedFiles());
        
        if (dto.getTargetGroupId() != null) {
            // загрузить Group entity по ID
        }
        assignment.setTargetStudentIds(dto.getTargetStudentIds());
        
        return assignmentRepository.save(assignment);
    }

    /** Задания преподавателя */
    @Transactional(readOnly = true)
    public Page<Assignment> getTeacherAssignments(UUID teacherId, Pageable pageable) {
        return assignmentRepository.findByCreatedByIdOrderByCreatedAtDesc(teacherId, pageable);
    }

    /** Задания для студента (через группу или прямое назначение) */
    @Transactional(readOnly = true)
    public Page<Assignment> getStudentAssignments(UUID studentId, UUID groupId, Pageable pageable) {
        return assignmentRepository.findAssignmentsForStudent(studentId, groupId, pageable);
    }

    /** Сдачи по заданию (для преподавателя) */
    @Transactional(readOnly = true)
    public Page<AssignmentSubmission> getSubmissions(UUID assignmentId, Pageable pageable) {
        return submissionRepository.findByAssignmentIdOrderBySubmittedAtDesc(assignmentId, pageable);
    }

    /** Создать сдачу (студент) */
    public AssignmentSubmission submitAssignment(UUID assignmentId, UUID studentId, SubmissionDto dto) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new EntityNotFoundException("Задание не найдено"));
        
        // Проверка дедлайна
        if (assignment.getDeadline() != null && 
            ZonedDateTime.now().isAfter(assignment.getDeadline()) && 
            !Boolean.TRUE.equals(assignment.getAllowLateSubmission())) {
            throw new IllegalStateException("Дедлайн прошёл, сдача невозможна");
        }
        
        // Проверка существующей сдачи
        var existing = submissionRepository.findFirstByAssignmentIdAndStudentIdOrderByVersionDesc(assignmentId, studentId);
        
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(new AppUser(studentId)); // lightweight ref
        submission.setSolutionFiles(dto.getSolutionFiles());
        submission.setStudentComment(dto.getStudentComment());
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setSubmittedAt(ZonedDateTime.now());
        
        if (existing.isPresent()) {
            submission.setVersion(existing.get().getVersion() + 1);
            submission.setPreviousVersion(existing.get());
        } else {
            submission.setVersion(1);
        }
        
        return submissionRepository.save(submission);
    }

    /** Оценить сдачу (преподаватель) */
    public AssignmentSubmission reviewSubmission(UUID submissionId, ReviewDto dto, AppUser teacher) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() -> new EntityNotFoundException("Сдача не найдена"));
        
        if (Boolean.TRUE.equals(dto.getReturnForRevision())) {
            submission.setStatus(SubmissionStatus.RETURNED);
        } else {
            submission.setStatus(SubmissionStatus.REVIEWED);
            submission.setScore(dto.getScore());
            submission.setTotalScore(dto.getTotalScore());
        }
        
        submission.setTeacherFeedback(dto.getTeacherFeedback());
        submission.setTeacherComment(dto.getTeacherComment());
        submission.setReviewedBy(teacher);
        submission.setReviewedAt(ZonedDateTime.now());
        
        return submissionRepository.save(submission);
    }

    /** Сдачи студента */
    @Transactional(readOnly = true)
    public Page<AssignmentSubmission> getStudentSubmissions(UUID studentId, Pageable pageable) {
        return submissionRepository.findByStudentIdOrderBySubmittedAtDesc(studentId, pageable);
    }
}
