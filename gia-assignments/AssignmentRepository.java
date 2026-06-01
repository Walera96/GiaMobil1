package com.spbutu.gia.assignments.repository;

import com.spbutu.gia.assignments.domain.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {

    /** Задания, созданные преподавателем */
    Page<Assignment> findByCreatedByIdOrderByCreatedAtDesc(UUID createdById, Pageable pageable);

    /** Задания для конкретной группы */
    Page<Assignment> findByTargetGroupIdOrderByDeadlineDesc(UUID targetGroupId, Pageable pageable);

    /** Задания, назначенные конкретному студенту (прямое назначение или через группу) */
    @Query("""
        SELECT DISTINCT a FROM Assignment a
        LEFT JOIN a.targetGroup g
        WHERE a.targetGroup.id = :groupId
           OR :studentId MEMBER OF a.targetStudentIds
        ORDER BY a.deadline DESC
        """)
    Page<Assignment> findAssignmentsForStudent(
        @Param("studentId") UUID studentId,
        @Param("groupId") UUID groupId,
        Pageable pageable
    );

    /** Активные задания с дедлайном в ближайшие N дней */
    @Query("""
        SELECT a FROM Assignment a
        WHERE a.deadline BETWEEN CURRENT_TIMESTAMP AND :futureDate
        AND (a.targetGroup.id = :groupId OR :studentId MEMBER OF a.targetStudentIds)
        ORDER BY a.deadline ASC
        """)
    List<Assignment> findUpcomingAssignments(
        @Param("studentId") UUID studentId,
        @Param("groupId") UUID groupId,
        @Param("futureDate") java.time.ZonedDateTime futureDate
    );
}
