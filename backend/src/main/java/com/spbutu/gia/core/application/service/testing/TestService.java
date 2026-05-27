package com.spbutu.gia.core.application.service.testing;

import com.spbutu.gia.core.application.dto.testing.*;
import com.spbutu.gia.core.domain.entity.testing.*;
import com.spbutu.gia.core.domain.repository.DisciplineRepository;
import com.spbutu.gia.core.domain.repository.testing.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class TestService {

    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final StudentTestAttemptRepository attemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final DisciplineRepository disciplineRepository;

    public TestService(TestRepository testRepository,
                       QuestionRepository questionRepository,
                       AnswerOptionRepository answerOptionRepository,
                       StudentTestAttemptRepository attemptRepository,
                       StudentAnswerRepository studentAnswerRepository,
                       DisciplineRepository disciplineRepository) {
        this.testRepository = testRepository;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.attemptRepository = attemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.disciplineRepository = disciplineRepository;
    }

    // ========== TEST CRUD ==========

    @Transactional(readOnly = true)
    public List<TestDto> getAllTests() {
        return testRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toTestDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestDto getTestById(UUID id) {
        return testRepository.findById(id)
                .map(this::toTestDto)
                .orElseThrow(() -> new IllegalArgumentException("Тест не найден: " + id));
    }

    @Transactional
    public TestDto createTest(CreateTestRequest request, UUID createdBy) {
        Test test = new Test();
        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setDisciplineId(request.getDisciplineId());
        test.setDirectionId(request.getDirectionId());
        test.setDurationMinutes(request.getDurationMinutes());
        test.setPassingScore(request.getPassingScore());
        test.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        test.setCreatedBy(createdBy);

        int maxScore = 0;
        if (request.getQuestions() != null) {
            for (CreateQuestionRequest qReq : request.getQuestions()) {
                Question q = new Question();
                q.setTest(test);
                q.setText(qReq.getText());
                q.setType(qReq.getType());
                q.setPoints(qReq.getPoints());
                q.setOrderNumber(qReq.getOrderNumber());
                maxScore += qReq.getPoints() != null ? qReq.getPoints() : 0;

                if (qReq.getOptions() != null) {
                    for (CreateOptionRequest oReq : qReq.getOptions()) {
                        AnswerOption opt = new AnswerOption();
                        opt.setQuestion(q);
                        opt.setText(oReq.getText());
                        opt.setIsCorrect(oReq.getIsCorrect() != null ? oReq.getIsCorrect() : false);
                        q.getOptions().add(opt);
                    }
                }
                test.getQuestions().add(q);
            }
        }
        test.setMaxScore(maxScore);

        return toTestDto(testRepository.save(test));
    }

    @Transactional
    public void deleteTest(UUID id) {
        testRepository.deleteById(id);
    }

    // ========== STUDENT TESTING ==========

    @Transactional
    public TestAttemptDto startTest(UUID studentId, UUID testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Тест не найден"));

        StudentTestAttempt attempt = new StudentTestAttempt();
        attempt.setStudentId(studentId);
        attempt.setTest(test);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus("STARTED");
        attempt = attemptRepository.save(attempt);

        return toAttemptDto(attempt);
    }

    @Transactional
    public TestResultDto submitTest(UUID studentId, SubmitTestRequest request) {
        StudentTestAttempt attempt = attemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new IllegalArgumentException("Попытка не найдена"));

        if (!studentId.equals(attempt.getStudentId())) {
            throw new IllegalArgumentException("Доступ запрещён");
        }

        Test test = attempt.getTest();
        List<Question> questions = questionRepository.findAllByTestId(test.getId());
        Map<UUID, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        int totalScore = 0;
        int totalCorrect = 0;
        List<AnswerResultDto> answerResults = new ArrayList<>();

        for (SubmitTestRequest.StudentAnswerInput input : request.getAnswers()) {
            Question question = questionMap.get(input.getQuestionId());
            if (question == null) continue;

            StudentAnswer sa = new StudentAnswer();
            sa.setAttempt(attempt);
            sa.setQuestionId(input.getQuestionId());
            sa.setSelectedOptionId(input.getSelectedOptionId());
            sa.setTextAnswer(input.getTextAnswer());

            boolean isCorrect = false;
            int pointsEarned = 0;
            String correctAnswer = "";
            String studentAnswer = "";

            if ("SINGLE_CHOICE".equals(question.getType()) && input.getSelectedOptionId() != null) {
                AnswerOption selected = answerOptionRepository.findById(input.getSelectedOptionId()).orElse(null);
                if (selected != null) {
                    studentAnswer = selected.getText();
                    isCorrect = Boolean.TRUE.equals(selected.getIsCorrect());
                }
                AnswerOption correctOpt = question.getOptions().stream()
                        .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                        .findFirst().orElse(null);
                if (correctOpt != null) correctAnswer = correctOpt.getText();
            } else if ("TEXT_ANSWER".equals(question.getType())) {
                studentAnswer = input.getTextAnswer();
                // Для текстовых ответов требуется ручная проверка
                isCorrect = false;
            }

            if (isCorrect) {
                pointsEarned = question.getPoints() != null ? question.getPoints() : 0;
                totalCorrect++;
            }
            totalScore += pointsEarned;

            sa.setIsCorrect(isCorrect);
            sa.setPointsEarned(pointsEarned);
            studentAnswerRepository.save(sa);

            AnswerResultDto result = new AnswerResultDto();
            result.setQuestionId(question.getId());
            result.setQuestionText(question.getText());
            result.setIsCorrect(isCorrect);
            result.setPointsEarned(pointsEarned);
            result.setMaxPoints(question.getPoints());
            result.setCorrectAnswer(correctAnswer);
            result.setStudentAnswer(studentAnswer);
            answerResults.add(result);
        }

        attempt.setScore(totalScore);
        attempt.setTotalCorrect(totalCorrect);
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setStatus("COMPLETED");
        attemptRepository.save(attempt);

        TestResultDto result = new TestResultDto();
        result.setAttemptId(attempt.getId());
        result.setTestId(test.getId());
        result.setTestTitle(test.getTitle());
        result.setScore(totalScore);
        result.setMaxScore(test.getMaxScore());
        result.setPassingScore(test.getPassingScore());
        result.setPassed(test.getPassingScore() != null && totalScore >= test.getPassingScore());
        result.setTotalCorrect(totalCorrect);
        result.setTotalQuestions(questions.size());
        result.setStatus("COMPLETED");
        result.setAnswerResults(answerResults);
        return result;
    }

    @Transactional(readOnly = true)
    public List<TestAttemptDto> getStudentAttempts(UUID studentId) {
        return attemptRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(this::toAttemptDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestResultDto getAttemptResult(UUID attemptId) {
        StudentTestAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Попытка не найдена"));

        Test test = attempt.getTest();
        List<StudentAnswer> answers = studentAnswerRepository.findAllByAttemptId(attemptId);
        List<AnswerResultDto> answerResults = new ArrayList<>();

        for (StudentAnswer sa : answers) {
            Question q = questionRepository.findById(sa.getQuestionId()).orElse(null);
            if (q == null) continue;

            AnswerResultDto dto = new AnswerResultDto();
            dto.setQuestionId(q.getId());
            dto.setQuestionText(q.getText());
            dto.setIsCorrect(sa.getIsCorrect());
            dto.setPointsEarned(sa.getPointsEarned());
            dto.setMaxPoints(q.getPoints());

            String correctAnswer = "";
            String studentAnswer = "";
            if ("SINGLE_CHOICE".equals(q.getType())) {
                AnswerOption correctOpt = q.getOptions().stream()
                        .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                        .findFirst().orElse(null);
                if (correctOpt != null) correctAnswer = correctOpt.getText();
                if (sa.getSelectedOptionId() != null) {
                    AnswerOption selected = answerOptionRepository.findById(sa.getSelectedOptionId()).orElse(null);
                    if (selected != null) studentAnswer = selected.getText();
                }
            } else {
                studentAnswer = sa.getTextAnswer();
            }
            dto.setCorrectAnswer(correctAnswer);
            dto.setStudentAnswer(studentAnswer);
            answerResults.add(dto);
        }

        TestResultDto result = new TestResultDto();
        result.setAttemptId(attempt.getId());
        result.setTestId(test.getId());
        result.setTestTitle(test.getTitle());
        result.setScore(attempt.getScore());
        result.setMaxScore(test.getMaxScore());
        result.setPassingScore(test.getPassingScore());
        result.setPassed(test.getPassingScore() != null && attempt.getScore() != null && attempt.getScore() >= test.getPassingScore());
        result.setTotalCorrect(attempt.getTotalCorrect());
        result.setTotalQuestions(questionRepository.findAllByTestId(test.getId()).size());
        result.setStatus(attempt.getStatus());
        result.setAnswerResults(answerResults);
        return result;
    }

    // ========== MAPPING ==========

    private TestDto toTestDto(Test test) {
        TestDto dto = new TestDto();
        dto.setId(test.getId());
        dto.setTitle(test.getTitle());
        dto.setDescription(test.getDescription());
        dto.setDisciplineId(test.getDisciplineId());
        if (test.getDisciplineId() != null) {
            disciplineRepository.findById(test.getDisciplineId()).ifPresent(d -> dto.setDisciplineName(d.getName()));
        }
        dto.setDirectionId(test.getDirectionId());
        dto.setDurationMinutes(test.getDurationMinutes());
        dto.setPassingScore(test.getPassingScore());
        dto.setMaxScore(test.getMaxScore());
        dto.setStatus(test.getStatus());
        dto.setQuestionCount(test.getQuestions() != null ? test.getQuestions().size() : 0);
        dto.setCreatedAt(test.getCreatedAt());

        List<QuestionDto> qDtos = new ArrayList<>();
        if (test.getQuestions() != null) {
            for (Question q : test.getQuestions()) {
                QuestionDto qDto = new QuestionDto();
                qDto.setId(q.getId());
                qDto.setText(q.getText());
                qDto.setType(q.getType());
                qDto.setPoints(q.getPoints());
                qDto.setOrderNumber(q.getOrderNumber());

                List<AnswerOptionDto> oDtos = new ArrayList<>();
                if (q.getOptions() != null) {
                    for (AnswerOption opt : q.getOptions()) {
                        AnswerOptionDto oDto = new AnswerOptionDto();
                        oDto.setId(opt.getId());
                        oDto.setText(opt.getText());
                        oDto.setIsCorrect(opt.getIsCorrect());
                        oDtos.add(oDto);
                    }
                }
                qDto.setOptions(oDtos);
                qDtos.add(qDto);
            }
        }
        dto.setQuestions(qDtos);
        return dto;
    }

    private TestAttemptDto toAttemptDto(StudentTestAttempt attempt) {
        TestAttemptDto dto = new TestAttemptDto();
        dto.setId(attempt.getId());
        dto.setTestId(attempt.getTest() != null ? attempt.getTest().getId() : null);
        dto.setTestTitle(attempt.getTest() != null ? attempt.getTest().getTitle() : null);
        dto.setScore(attempt.getScore());
        dto.setMaxScore(attempt.getTest() != null ? attempt.getTest().getMaxScore() : null);
        dto.setTotalCorrect(attempt.getTotalCorrect());
        dto.setTotalQuestions(attempt.getTest() != null && attempt.getTest().getQuestions() != null ? attempt.getTest().getQuestions().size() : 0);
        dto.setStatus(attempt.getStatus());
        dto.setStartedAt(attempt.getStartedAt());
        dto.setFinishedAt(attempt.getFinishedAt());
        dto.setCreatedAt(attempt.getCreatedAt());
        return dto;
    }
}
