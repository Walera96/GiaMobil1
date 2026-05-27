package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.CreateGradeRequest;
import com.spbutu.gia.core.application.dto.GradeDto;
import com.spbutu.gia.core.domain.entity.Discipline;
import com.spbutu.gia.core.domain.entity.Grade;
import com.spbutu.gia.core.domain.entity.Student;
import com.spbutu.gia.core.domain.repository.DisciplineRepository;
import com.spbutu.gia.core.domain.repository.GradeRepository;
import com.spbutu.gia.core.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final DisciplineRepository disciplineRepository;

    public GradeService(GradeRepository gradeRepository, StudentRepository studentRepository, DisciplineRepository disciplineRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.disciplineRepository = disciplineRepository;
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findAllByStudent(UUID studentId) {
        return gradeRepository.findAllByStudentId(studentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findAllByDiscipline(UUID disciplineId) {
        return gradeRepository.findAllByDisciplineId(disciplineId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findAllBySemester(String semester) {
        return gradeRepository.findAllBySemester(semester).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findAll() {
        return gradeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GradeDto> findAllByGroup(UUID groupId) {
        return gradeRepository.findAllByStudentGroupId(groupId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeDto create(CreateGradeRequest request) {
        Grade grade = new Grade();
        return saveGrade(grade, request);
    }

    @Transactional
    public GradeDto update(UUID id, CreateGradeRequest request) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена"));
        return saveGrade(grade, request);
    }

    @Transactional
    public void delete(UUID id) {
        gradeRepository.deleteById(id);
    }

    private GradeDto saveGrade(Grade grade, CreateGradeRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Студент не найден"));
        grade.setStudent(student);

        if (request.getDisciplineId() != null) {
            Discipline discipline = disciplineRepository.findById(request.getDisciplineId())
                    .orElseThrow(() -> new RuntimeException("Дисциплина не найдена"));
            grade.setDiscipline(discipline);
            grade.setSubjectName(discipline.getName());
        } else if (request.getSubjectName() != null) {
            grade.setSubjectName(request.getSubjectName());
        }

        grade.setScore(request.getScore());
        grade.setCurrentControl(request.getCurrentControl());
        grade.setAttendance(request.getAttendance());
        grade.setActivity(request.getActivity());
        grade.setExamScore(request.getExamScore());
        grade.setSemester(request.getSemester());

        calculateTotals(grade);

        return toDto(gradeRepository.save(grade));
    }

    public void calculateTotals(Grade grade) {
        int total = 0;
        if (grade.getCurrentControl() != null) total += grade.getCurrentControl();
        if (grade.getAttendance() != null) total += grade.getAttendance();
        if (grade.getActivity() != null) total += grade.getActivity();
        if (grade.getExamScore() != null) total += grade.getExamScore();
        grade.setTotalScore(total);

        int max = 130;
        int percent = max > 0 ? (int) Math.round((double) total / max * 100) : 0;

        grade.setFivePointGrade(calculateFivePoint(percent));
        grade.setEctsGrade(calculateEcts(percent));
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

    private GradeDto toDto(Grade grade) {
        GradeDto dto = new GradeDto();
        dto.setId(grade.getId());
        dto.setStudentId(grade.getStudent().getId());
        dto.setStudentName(grade.getStudent().getLastName() + " " + grade.getStudent().getFirstName());
        if (grade.getDiscipline() != null) {
            dto.setDisciplineId(grade.getDiscipline().getId());
            dto.setDisciplineName(grade.getDiscipline().getName());
        }
        dto.setSubjectName(grade.getSubjectName());
        dto.setScore(grade.getScore());
        dto.setCurrentControl(grade.getCurrentControl());
        dto.setAttendance(grade.getAttendance());
        dto.setActivity(grade.getActivity());
        dto.setExamScore(grade.getExamScore());
        dto.setTotalScore(grade.getTotalScore());
        dto.setEctsGrade(grade.getEctsGrade());
        dto.setFivePointGrade(grade.getFivePointGrade());
        dto.setSemester(grade.getSemester());
        return dto;
    }
}
