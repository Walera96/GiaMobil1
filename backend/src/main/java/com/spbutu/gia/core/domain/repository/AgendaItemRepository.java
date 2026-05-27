package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.AgendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий пунктов повестки заседаний.
 */
@Repository
public interface AgendaItemRepository extends JpaRepository<AgendaItem, UUID> {

    List<AgendaItem> findAllByMeetingId(UUID meetingId);

    List<AgendaItem> findAllByStudentId(UUID studentId);
}
