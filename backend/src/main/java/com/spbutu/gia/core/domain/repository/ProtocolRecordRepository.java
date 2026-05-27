package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.ProtocolRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий записей протокола (итоговых оценок).
 */
@Repository
public interface ProtocolRecordRepository extends JpaRepository<ProtocolRecord, UUID> {

    List<ProtocolRecord> findAllByProtocolId(UUID protocolId);

    Optional<ProtocolRecord> findByProtocolIdAndStudentId(UUID protocolId, UUID studentId);

    List<ProtocolRecord> findByStudentId(UUID studentId);
}
