package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.GekMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий составов ГЭК (связь члена с комиссией).
 */
@Repository
public interface GekMembershipRepository extends JpaRepository<GekMembership, UUID> {

    List<GekMembership> findAllByGekId(UUID gekId);
}
