package com.spbutu.gia.assignments.domain.repository;

import com.spbutu.gia.assignments.domain.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    List<Assignment> findAllByCreatedByOrderByCreatedAtDesc(UUID createdBy);

    List<Assignment> findAllByTargetGroupId(UUID targetGroupId);

    @Query(value = "SELECT * FROM assignments WHERE target_student_ids @> to_jsonb(array[:studentId]::uuid[])", nativeQuery = true)
    List<Assignment> findAllByTargetStudentIdsContains(@Param("studentId") UUID studentId);

    @Query(value = "SELECT * FROM assignments WHERE target_group_id = :groupId OR target_student_ids @> to_jsonb(array[:studentId]::uuid[])", nativeQuery = true)
    List<Assignment> findAllForStudent(@Param("groupId") UUID groupId, @Param("studentId") UUID studentId);
}
