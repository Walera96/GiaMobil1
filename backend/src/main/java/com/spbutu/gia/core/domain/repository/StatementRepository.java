package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
    List<Statement> findByGroupId(UUID groupId);
    List<Statement> findByStatus(String status);
    List<Statement> findByGroupIdAndStatus(UUID groupId, String status);
    List<Statement> findByDisciplineId(UUID disciplineId);
}
