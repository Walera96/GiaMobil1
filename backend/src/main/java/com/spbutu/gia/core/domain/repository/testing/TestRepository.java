package com.spbutu.gia.core.domain.repository.testing;

import com.spbutu.gia.core.domain.entity.testing.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<Test, UUID> {

    List<Test> findAllByStatus(String status);

    List<Test> findAllByDisciplineId(UUID disciplineId);

    List<Test> findAllByDirectionId(UUID directionId);

    List<Test> findAllByOrderByCreatedAtDesc();
}
