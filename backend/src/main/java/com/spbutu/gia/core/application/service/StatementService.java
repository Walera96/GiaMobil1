package com.spbutu.gia.core.application.service;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.core.application.dto.*;
import com.spbutu.gia.core.domain.entity.*;
import com.spbutu.gia.core.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class StatementService {

    private final StatementRepository statementRepository;
    private final StatementRecordRepository statementRecordRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final DisciplineRepository disciplineRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    public StatementService(StatementRepository statementRepository,
                            StatementRecordRepository statementRecordRepository,
                            StudyGroupRepository studyGroupRepository,
                            DisciplineRepository disciplineRepository,
                            TeacherRepository teacherRepository,
                            StudentRepository studentRepository) {
        this.statementRepository = statementRepository;
        this.statementRecordRepository = statementRecordRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.disciplineRepository = disciplineRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StatementDto> findAll() {
        return statementRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StatementDto findById(UUID id) {
        return statementRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Ведомость не найдена"));
    }

    @Transactional
    public StatementDto create(CreateStatementRequest request, AppUser currentUser) {
        Statement statement = new Statement();
        statement.setStatementNumber(request.getStatementNumber());
        statement.setAcademicYear(request.getAcademicYear());
        statement.setSemester(request.getSemester());
        statement.setCreatedBy(currentUser);
        statement.setStatus("DRAFT");

        StudyGroup group = studyGroupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
        statement.setGroup(group);

        if (request.getDisciplineId() != null) {
            Discipline discipline = disciplineRepository.findById(request.getDisciplineId())
                    .orElseThrow(() -> new RuntimeException("Дисциплина не найдена"));
            statement.setDiscipline(discipline);
        }

        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Преподаватель не найден"));
            statement.setTeacher(teacher);
        }

        List<Student> students = studentRepository.findAllByGroupId(group.getId());
        for (Student student : students) {
            StatementRecord record = new StatementRecord();
            record.setStudent(student);
            statement.addRecord(record);
        }

        return toDto(statementRepository.save(statement));
    }

    @Transactional
    public StatementDto update(UUID id, CreateStatementRequest request) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ведомость не найдена"));

        statement.setStatementNumber(request.getStatementNumber());
        statement.setAcademicYear(request.getAcademicYear());
        statement.setSemester(request.getSemester());

        if (request.getGroupId() != null) {
            StudyGroup group = studyGroupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Группа не найдена"));
            statement.setGroup(group);
        }

        if (request.getDisciplineId() != null) {
            Discipline discipline = disciplineRepository.findById(request.getDisciplineId())
                    .orElseThrow(() -> new RuntimeException("Дисциплина не найдена"));
            statement.setDiscipline(discipline);
        }

        if (request.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Преподаватель не найден"));
            statement.setTeacher(teacher);
        }

        return toDto(statementRepository.save(statement));
    }

    @Transactional
    public StatementDto updateRecord(UUID statementId, UUID recordId, StatementRecordDto recordDto) {
        Statement statement = statementRepository.findById(statementId)
                .orElseThrow(() -> new RuntimeException("Ведомость не найдена"));

        StatementRecord record = statementRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Запись не найдена"));

        record.setCurrentControl(recordDto.getCurrentControl());
        record.setAttendance(recordDto.getAttendance());
        record.setActivity(recordDto.getActivity());
        record.setExamScore(recordDto.getExamScore());

        int total = 0;
        if (record.getCurrentControl() != null) total += record.getCurrentControl();
        if (record.getAttendance() != null) total += record.getAttendance();
        if (record.getActivity() != null) total += record.getActivity();
        if (record.getExamScore() != null) total += record.getExamScore();
        record.setTotalScore(total);

        int max = 130;
        int percent = max > 0 ? (int) Math.round((double) total / max * 100) : 0;
        record.setFivePointGrade(calculateFivePoint(percent));
        record.setEctsGrade(calculateEcts(percent));

        return toDto(statementRepository.save(statement));
    }

    @Transactional
    public StatementDto changeStatus(UUID id, String status) {
        Statement statement = statementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ведомость не найдена"));
        statement.setStatus(status);
        return toDto(statementRepository.save(statement));
    }

    @Transactional
    public void delete(UUID id) {
        statementRepository.deleteById(id);
    }

    private int calculateFivePoint(int percent) {
        if (percent >= 85) return 5;
        if (percent >= 70) return 4;
        if (percent >= 60) return 3;
        return 2;
    }

    private String calculateEcts(int percent) {
        if (percent >= 90) return "A";
        if (percent >= 82) return "B";
        if (percent >= 74) return "C";
        if (percent >= 64) return "D";
        if (percent >= 60) return "E";
        return "F";
    }

    private StatementDto toDto(Statement statement) {
        StatementDto dto = new StatementDto();
        dto.setId(statement.getId());
        dto.setStatementNumber(statement.getStatementNumber());
        dto.setAcademicYear(statement.getAcademicYear());
        dto.setSemester(statement.getSemester());
        if (statement.getGroup() != null) {
            dto.setGroupId(statement.getGroup().getId());
            dto.setGroupName(statement.getGroup().getName());
        }
        if (statement.getDiscipline() != null) {
            dto.setDisciplineId(statement.getDiscipline().getId());
            dto.setDisciplineName(statement.getDiscipline().getName());
        }
        if (statement.getTeacher() != null) {
            dto.setTeacherId(statement.getTeacher().getId());
            dto.setTeacherName(statement.getTeacher().getFullName());
        }
        dto.setStatus(statement.getStatus());
        dto.setRecords(statement.getRecords().stream()
                .map(this::toRecordDto)
                .collect(Collectors.toList()));
        dto.setCreatedAt(statement.getCreatedAt());
        dto.setUpdatedAt(statement.getUpdatedAt());
        return dto;
    }

    private StatementRecordDto toRecordDto(StatementRecord record) {
        StatementRecordDto dto = new StatementRecordDto();
        dto.setId(record.getId());
        dto.setStudentId(record.getStudent().getId());
        dto.setStudentName(record.getStudent().getLastName() + " " + record.getStudent().getFirstName());
        dto.setRecordBookNumber(record.getStudent().getRecordBookNumber());
        dto.setCurrentControl(record.getCurrentControl());
        dto.setAttendance(record.getAttendance());
        dto.setActivity(record.getActivity());
        dto.setExamScore(record.getExamScore());
        dto.setTotalScore(record.getTotalScore());
        dto.setEctsGrade(record.getEctsGrade());
        dto.setFivePointGrade(record.getFivePointGrade());
        return dto;
    }
}
