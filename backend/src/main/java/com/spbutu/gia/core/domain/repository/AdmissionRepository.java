package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Admission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий допусков к аттестации.
 */
@Repository
public interface AdmissionRepository extends JpaRepository<Admission, UUID> {

    Optional<Admission> findByStudentId(UUID studentId);

    List<Admission> findByIsAdmittedTrue();
}
