package com.spbutu.gia.assignments;

import com.spbutu.gia.assignments.application.dto.AssignmentCreateDto;
import com.spbutu.gia.assignments.application.dto.ReviewDto;
import com.spbutu.gia.assignments.application.service.AssignmentService;
import com.spbutu.gia.assignments.application.service.AssignmentSubmissionService;
import com.spbutu.gia.assignments.domain.entity.Assignment;
import com.spbutu.gia.assignments.domain.entity.AssignmentSubmission;
import com.spbutu.gia.assignments.domain.enums.AssignmentType;
import com.spbutu.gia.assignments.domain.enums.SubmissionStatus;
import com.spbutu.gia.assignments.domain.repository.AssignmentRepository;
import com.spbutu.gia.assignments.domain.repository.AssignmentSubmissionRepository;
import com.spbutu.gia.assignments.domain.vo.ScoringConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Интеграционный тест модуля assignments.
 * Полный цикл: создание задания → сдача → проверка → оценка.
 * Использует TestContainers для PostgreSQL.
 */
@SpringBootTest
@Testcontainers
class AssignmentIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("gia_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentSubmissionService submissionService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    private final UUID teacherId = UUID.randomUUID();
    private final UUID studentId = UUID.randomUUID();
    private final UUID groupId = UUID.randomUUID();

    @Test
    @SuppressWarnings("null")
    void fullLifecycle_CreateSubmitReview_ShouldWork() {
        // === 1. Создание задания ===
        ScoringConfig scoringConfig = new ScoringConfig();
        scoringConfig.setType("weighted");
        ScoringConfig.ScoringCriteria criteria = new ScoringConfig.ScoringCriteria();
        criteria.setName("Качество кода");
        criteria.setWeight(50);
        criteria.setMaxPoints(50);
        scoringConfig.setCriteria(List.of(criteria));
        scoringConfig.setMaxTotalScore(100);
        scoringConfig.setPassingScore(60);

        AssignmentCreateDto createDto = new AssignmentCreateDto(
                "Тестовое задание",
                "Описание тестового задания",
                AssignmentType.LAB,
                groupId,
                List.of(studentId),
                ZonedDateTime.now().plusDays(7),
                false,
                100,
                scoringConfig,
                List.of()
        );

        var assignmentDto = assignmentService.create(createDto, teacherId);
        assertThat(assignmentDto).isNotNull();
        assertThat(assignmentDto.title()).isEqualTo("Тестовое задание");

        UUID assignmentId = assignmentDto.id();

        // === 2. Проверка, что задание сохранено ===
        Assignment assignment = assignmentRepository.findById(assignmentId).orElseThrow();
        assertThat(assignment.getTitle()).isEqualTo("Тестовое задание");
        assertThat(assignment.getMaxScore()).isEqualTo(100);

        // === 3. Сдача работы студентом ===
        var submissionDto = submissionService.submit(
                assignmentId,
                studentId,
                List.of(),
                "Выполнил все пункты задания"
        );
        assertThat(submissionDto).isNotNull();
        assertThat(submissionDto.status()).isEqualTo(SubmissionStatus.SUBMITTED);

        UUID submissionId = submissionDto.id();

        // === 4. Проверка сдачи преподавателем ===
        ReviewDto reviewDto = new ReviewDto(
                85,
                List.of(),
                "Отличная работа! Небольшие замечания по оформлению.",
                "Внутренний комментарий",
                false
        );

        var reviewedSubmission = submissionService.review(submissionId, reviewDto, teacherId);
        assertThat(reviewedSubmission).isNotNull();
        assertThat(reviewedSubmission.status()).isEqualTo(SubmissionStatus.REVIEWED);
        assertThat(reviewedSubmission.totalScore()).isEqualTo(85);

        // === 5. Проверка данных в БД ===
        AssignmentSubmission saved = submissionRepository.findById(submissionId).orElseThrow();
        assertThat(saved.getStatus()).isEqualTo(SubmissionStatus.REVIEWED);
        assertThat(saved.getTotalScore()).isEqualTo(85);
        assertThat(saved.getTeacherFeedback()).contains("Отличная работа");
        assertThat(saved.getReviewedBy()).isEqualTo(teacherId);
    }

    @Test
    void createAssignment_WithInvalidDeadline_ShouldFail() {
        AssignmentCreateDto createDto = new AssignmentCreateDto(
                "Просроченное задание",
                "Описание",
                AssignmentType.HOMEWORK,
                groupId,
                null,
                ZonedDateTime.now().minusDays(1), // дедлайн в прошлом
                false,
                10,
                null,
                List.of()
        );

        try {
            assignmentService.create(createDto, teacherId);
            fail("Должно было выброситься исключение из-за дедлайна в прошлом");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).containsIgnoringCase("дедлайн");
        }
    }
}
