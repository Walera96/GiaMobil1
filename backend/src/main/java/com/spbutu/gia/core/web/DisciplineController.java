package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.CreateDisciplineRequest;
import com.spbutu.gia.core.application.dto.DisciplineDto;
import com.spbutu.gia.core.application.service.DisciplineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/disciplines")
@SuppressWarnings("null")
public class DisciplineController {

    private final DisciplineService disciplineService;

    public DisciplineController(DisciplineService disciplineService) {
        this.disciplineService = disciplineService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY','GEK_CHAIRMAN','GEK_MEMBER','STUDENT')")
    public ResponseEntity<List<DisciplineDto>> getAll() {
        return ResponseEntity.ok(disciplineService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST','GEK_SECRETARY','GEK_CHAIRMAN','GEK_MEMBER','STUDENT')")
    public ResponseEntity<DisciplineDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(disciplineService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST')")
    public ResponseEntity<DisciplineDto> create(@RequestBody CreateDisciplineRequest request) {
        return ResponseEntity.ok(disciplineService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST')")
    public ResponseEntity<DisciplineDto> update(@PathVariable UUID id, @RequestBody CreateDisciplineRequest request) {
        return ResponseEntity.ok(disciplineService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        disciplineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
