package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий голосов членов ГЭК.
 */
@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    List<Vote> findAllByAgendaItemId(UUID agendaItemId);
}
