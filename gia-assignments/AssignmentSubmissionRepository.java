package com.spbutu.gia.assignments.repository;

import com.spbutu.gia.assignments.domain.AssignmentSubmission;
import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {

    /** Все сдачи по заданию (для преподавателя) */
    Page<AssignmentSubmission> findByAssignmentIdOrderBySubmittedAtDesc(UUID assignmentId, Pageable pageable);

    /** Сдачи студента по конкретному заданию */
    List<AssignmentSubmission> findByAssignmentIdAndStudentIdOrderByVersionDesc(UUID assignmentId, UUID studentId);

    /** Последняя версия сдачи студента по заданию */
    Optional<AssignmentSubmission> findFirstByAssignmentIdAndStudentIdOrderByVersionDesc(UUID assignmentId, UUID studentId);

    /** Все сдачи студента */
    Page<AssignmentSubmission> findByStudentIdOrderBySubmittedAtDesc(UUID studentId, Pageable pageable);

    /** Сдачи со статусом SUBMITTED или REVIEWING (для преподавателя — что проверять) */
    Page<AssignmentSubmission> findByAssignmentIdAndStatusIn(
        UUID assignmentId,
        List<SubmissionStatus> statuses,
        Pageable pageable
    );

    /** Количество сдач по статусу для задания */
    @Query("SELECT COUNT(s) FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.status = :status")
    Long countByAssignmentIdAndStatus(@Param("assignmentId") UUID assignmentId, @Param("status") SubmissionStatus status);

    /** Средний балл по заданию */
    @Query("SELECT AVG(s.totalScore) FROM AssignmentSubmission s WHERE s.assignment.id = :assignmentId AND s.status = 'REVIEWED'")
    Double getAverageScoreForAssignment(@Param("assignmentId") UUID assignmentId);
}
