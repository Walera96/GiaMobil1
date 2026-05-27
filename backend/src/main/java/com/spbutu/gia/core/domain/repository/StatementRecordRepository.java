package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.StatementRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatementRecordRepository extends JpaRepository<StatementRecord, UUID> {
    List<StatementRecord> findByStatementId(UUID statementId);
    List<StatementRecord> findByStudentId(UUID studentId);
}
