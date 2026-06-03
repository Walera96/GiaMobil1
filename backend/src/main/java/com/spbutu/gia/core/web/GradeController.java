package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.CreateGradeRequest;
import com.spbutu.gia.core.application.dto.GradeDto;
import com.spbutu.gia.core.application.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY','GEK_CHAIRMAN')")
    public ResponseEntity<List<GradeDto>> getAll() {
        return ResponseEntity.ok(gradeService.findAll());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY','GEK_CHAIRMAN','STUDENT')")
    public ResponseEntity<List<GradeDto>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(gradeService.findAllByStudent(studentId));
    }

    @GetMapping("/discipline/{disciplineId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY','GEK_CHAIRMAN')")
    public ResponseEntity<List<GradeDto>> getByDiscipline(@PathVariable UUID disciplineId) {
        return ResponseEntity.ok(gradeService.findAllByDiscipline(disciplineId));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY','GEK_CHAIRMAN')")
    public ResponseEntity<List<GradeDto>> getByGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(gradeService.findAllByGroup(groupId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY')")
    public ResponseEntity<GradeDto> create(@RequestBody CreateGradeRequest request) {
        return ResponseEntity.ok(gradeService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY')")
    public ResponseEntity<GradeDto> update(@PathVariable UUID id, @RequestBody CreateGradeRequest request) {
        return ResponseEntity.ok(gradeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
