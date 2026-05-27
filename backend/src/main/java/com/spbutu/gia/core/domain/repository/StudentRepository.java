package com.spbutu.gia.core.domain.repository;

import com.spbutu.gia.core.domain.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий студентов.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    List<Student> findAllByGroupId(UUID groupId);

    List<Student> findByLastNameContainingIgnoreCase(String lastName);

    Optional<Student> findByUserId(UUID userId);
}
