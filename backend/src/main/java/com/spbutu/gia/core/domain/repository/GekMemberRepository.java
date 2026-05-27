package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.GekMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий членов ГЭК (личных карточек преподавателей).
 */
@Repository
public interface GekMemberRepository extends JpaRepository<GekMember, UUID> {

    Optional<GekMember> findFirstByUserId(UUID userId);
}
