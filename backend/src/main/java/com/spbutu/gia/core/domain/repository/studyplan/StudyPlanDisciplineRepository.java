package com.spbutu.gia.core.domain.repository.studyplan;

import com.spbutu.gia.core.domain.entity.studyplan.StudyPlanDiscipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudyPlanDisciplineRepository extends JpaRepository<StudyPlanDiscipline, UUID> {

    List<StudyPlanDiscipline> findAllByStudyPlanId(UUID studyPlanId);

    void deleteAllByStudyPlanId(UUID studyPlanId);
}
