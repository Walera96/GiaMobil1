package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий оценок студентов.
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, UUID> {

    List<Grade> findAllByStudentId(UUID studentId);
    List<Grade> findAllByDisciplineId(UUID disciplineId);
    List<Grade> findAllByStudentIdAndDisciplineId(UUID studentId, UUID disciplineId);
    List<Grade> findAllBySemester(String semester);
    List<Grade> findAllByStudentGroupId(UUID groupId);
}
