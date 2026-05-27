package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Meeting;
import com.spbutu.gia.core.domain.enums.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий заседаний ГЭК.
 */
@Repository
public interface MeetingRepository extends JpaRepository<Meeting, UUID> {

    List<Meeting> findAllByStatus(MeetingStatus status);
}
