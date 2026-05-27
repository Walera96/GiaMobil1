package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.domain.entity.Student;
import com.spbutu.gia.core.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Сервис справочника студентов.
 */
@SuppressWarnings("null")
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByGroupId(UUID groupId) {
        return studentRepository.findAllByGroupId(groupId);
    }

    @Transactional(readOnly = true)
    public Student getStudent(UUID id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + id));
    }

    @Transactional
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Transactional
    public Student updateStudent(UUID id, Student updated) {
        Student student = getStudent(id);
        student.setLastName(updated.getLastName());
        student.setFirstName(updated.getFirstName());
        student.setMiddleName(updated.getMiddleName());
        student.setRecordBookNumber(updated.getRecordBookNumber());
        student.setGroup(updated.getGroup());
        return studentRepository.save(student);
    }

    @Transactional
    public void deleteStudent(UUID id) {
        studentRepository.deleteById(id);
    }
}
