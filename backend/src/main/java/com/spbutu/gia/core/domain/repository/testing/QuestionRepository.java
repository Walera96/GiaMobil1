package com.spbutu.gia.core.domain.repository.testing;

import com.spbutu.gia.core.domain.entity.testing.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    List<Question> findAllByTestId(UUID testId);
}
