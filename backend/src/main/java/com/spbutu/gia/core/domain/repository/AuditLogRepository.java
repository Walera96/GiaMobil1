package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий журнала аудита.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findAllByTableNameAndRecordId(String tableName, UUID recordId);
}
