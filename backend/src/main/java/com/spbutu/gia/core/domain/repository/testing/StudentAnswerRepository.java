package com.spbutu.gia.core.domain.repository.testing;

import com.spbutu.gia.core.domain.entity.testing.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, UUID> {

    List<StudentAnswer> findAllByAttemptId(UUID attemptId);
}
