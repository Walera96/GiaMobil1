package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, UUID> {
    List<Discipline> findByDirectionId(UUID directionId);
    List<Discipline> findByCourse(Integer course);
    List<Discipline> findBySemester(String semester);
}
