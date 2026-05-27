package com.spbutu.gia.core.domain.repository.testing;

import com.spbutu.gia.core.domain.entity.testing.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerOptionRepository extends JpaRepository<AnswerOption, UUID> {

    List<AnswerOption> findAllByQuestionId(UUID questionId);
}
