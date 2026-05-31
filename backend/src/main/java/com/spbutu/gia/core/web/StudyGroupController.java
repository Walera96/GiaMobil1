package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.StudyGroupService;
import com.spbutu.gia.core.domain.entity.StudyGroup;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study-groups")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    public StudyGroupController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY')")
    public ResponseEntity<List<StudyGroup>> getAll() {
        return ResponseEntity.ok(studyGroupService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY')")
    public ResponseEntity<StudyGroup> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(studyGroupService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<StudyGroup> create(@RequestBody StudyGroup group) {
        return ResponseEntity.ok(studyGroupService.create(group));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<StudyGroup> update(@PathVariable UUID id, @RequestBody StudyGroup group) {
        return ResponseEntity.ok(studyGroupService.update(id, group));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        studyGroupService.delete(id);
        return ResponseEntity.ok().build();
    }
}
