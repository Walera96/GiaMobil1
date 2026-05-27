package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий учебных групп.
 */
@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, UUID> {
}
