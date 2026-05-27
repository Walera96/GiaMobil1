package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.CreateTeacherRequest;
import com.spbutu.gia.core.application.dto.TeacherDto;
import com.spbutu.gia.core.domain.entity.Teacher;
import com.spbutu.gia.core.domain.repository.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public List<TeacherDto> findAll() {
        return teacherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TeacherDto findById(UUID id) {
        return teacherRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден"));
    }

    @Transactional
    public TeacherDto create(CreateTeacherRequest request) {
        Teacher teacher = new Teacher();
        teacher.setLastName(request.getLastName());
        teacher.setFirstName(request.getFirstName());
        teacher.setMiddleName(request.getMiddleName());
        teacher.setDepartment(request.getDepartment());
        teacher.setPosition(request.getPosition());
        teacher.setDegree(request.getDegree());
        teacher.setEmail(request.getEmail());
        return toDto(teacherRepository.save(teacher));
    }

    @Transactional
    public TeacherDto update(UUID id, CreateTeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Преподаватель не найден"));
        teacher.setLastName(request.getLastName());
        teacher.setFirstName(request.getFirstName());
        teacher.setMiddleName(request.getMiddleName());
        teacher.setDepartment(request.getDepartment());
        teacher.setPosition(request.getPosition());
        teacher.setDegree(request.getDegree());
        teacher.setEmail(request.getEmail());
        return toDto(teacherRepository.save(teacher));
    }

    @Transactional
    public void delete(UUID id) {
        teacherRepository.deleteById(id);
    }

    private TeacherDto toDto(Teacher teacher) {
        TeacherDto dto = new TeacherDto();
        dto.setId(teacher.getId());
        dto.setLastName(teacher.getLastName());
        dto.setFirstName(teacher.getFirstName());
        dto.setMiddleName(teacher.getMiddleName());
        dto.setFullName(teacher.getFullName());
        dto.setDepartment(teacher.getDepartment());
        dto.setPosition(teacher.getPosition());
        dto.setDegree(teacher.getDegree());
        dto.setEmail(teacher.getEmail());
        return dto;
    }
}
