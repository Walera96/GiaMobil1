package com.spbutu.gia.core.domain.repository.studyplan;

import com.spbutu.gia.core.domain.entity.studyplan.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudyPlanRepository extends JpaRepository<StudyPlan, UUID> {

    List<StudyPlan> findAllByDirectionId(UUID directionId);

    List<StudyPlan> findAllByStatus(String status);

    List<StudyPlan> findAllByOrderByCreatedAtDesc();
}
