package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Gek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий ГЭК.
 */
@Repository
public interface GekRepository extends JpaRepository<Gek, UUID> {
}
