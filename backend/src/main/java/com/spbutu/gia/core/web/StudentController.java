package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.StudentService;
import com.spbutu.gia.core.domain.entity.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер справочника студентов.
 */
@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY')")
    public ResponseEntity<List<Student>> getAllStudents(@RequestParam(required = false) UUID groupId) {
        if (groupId != null) {
            return ResponseEntity.ok(studentService.getStudentsByGroupId(groupId));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY', 'STUDENT')")
    public ResponseEntity<Student> getStudent(@PathVariable UUID id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Student> createStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.createStudent(student));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Student> updateStudent(@PathVariable UUID id, @RequestBody Student student) {
        return ResponseEntity.ok(studentService.updateStudent(id, student));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Void> deleteStudent(@PathVariable UUID id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok().build();
    }
}
