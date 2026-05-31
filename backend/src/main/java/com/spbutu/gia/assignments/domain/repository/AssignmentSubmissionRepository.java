package com.spbutu.gia.assignments.domain.repository;

import com.spbutu.gia.assignments.domain.entity.AssignmentSubmission;
import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, UUID> {

    List<AssignmentSubmission> findAllByAssignmentId(UUID assignmentId);

    List<AssignmentSubmission> findAllByStudentIdOrderByCreatedAtDesc(UUID studentId);

    Optional<AssignmentSubmission> findTopByAssignmentIdAndStudentIdOrderByVersionDesc(UUID assignmentId, UUID studentId);

    List<AssignmentSubmission> findAllByAssignmentIdAndStatus(UUID assignmentId, SubmissionStatus status);
}
