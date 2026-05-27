package com.spbutu.gia.core.domain.repository.testing;

import com.spbutu.gia.core.domain.entity.testing.StudentTestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentTestAttemptRepository extends JpaRepository<StudentTestAttempt, UUID> {

    List<StudentTestAttempt> findAllByStudentIdOrderByCreatedAtDesc(UUID studentId);

    List<StudentTestAttempt> findAllByTestId(UUID testId);

    List<StudentTestAttempt> findAllByStudentIdAndTestId(UUID studentId, UUID testId);
}
