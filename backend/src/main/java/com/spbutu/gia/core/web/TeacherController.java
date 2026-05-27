package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.CreateTeacherRequest;
import com.spbutu.gia.core.application.dto.TeacherDto;
import com.spbutu.gia.core.application.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teachers")
@SuppressWarnings("null")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY','CHAIRMAN','GEK_MEMBER')")
    public ResponseEntity<List<TeacherDto>> getAll() {
        return ResponseEntity.ok(teacherService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY','CHAIRMAN','GEK_MEMBER')")
    public ResponseEntity<TeacherDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(teacherService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST')")
    public ResponseEntity<TeacherDto> create(@RequestBody CreateTeacherRequest request) {
        return ResponseEntity.ok(teacherService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST')")
    public ResponseEntity<TeacherDto> update(@PathVariable UUID id, @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.ok(teacherService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
