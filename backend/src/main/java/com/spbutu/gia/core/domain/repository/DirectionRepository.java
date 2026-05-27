package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Direction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий направлений подготовки.
 */
@Repository
public interface DirectionRepository extends JpaRepository<Direction, UUID> {
    Optional<Direction> findByCode(String code);
}
