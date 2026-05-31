package com.spbutu.gia.core.web.studyplan;

import com.spbutu.gia.core.application.dto.studyplan.*;
import com.spbutu.gia.core.application.service.studyplan.StudyPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/study-plans")
@SuppressWarnings("null")
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    public StudyPlanController(StudyPlanService studyPlanService) {
        this.studyPlanService = studyPlanService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY', 'DEAN')")
    public ResponseEntity<List<StudyPlanDto>> getAllStudyPlans() {
        return ResponseEntity.ok(studyPlanService.getAllStudyPlans());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY', 'DEAN', 'STUDENT')")
    public ResponseEntity<StudyPlanDto> getStudyPlanById(@PathVariable UUID id) {
        return ResponseEntity.ok(studyPlanService.getStudyPlanById(id));
    }

    @GetMapping("/by-direction/{directionId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY', 'DEAN', 'STUDENT')")
    public ResponseEntity<List<StudyPlanDto>> getStudyPlansByDirection(@PathVariable UUID directionId) {
        return ResponseEntity.ok(studyPlanService.getStudyPlansByDirection(directionId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'DEAN')")
    public ResponseEntity<StudyPlanDto> createStudyPlan(@RequestBody CreateStudyPlanRequest request) {
        return ResponseEntity.ok(studyPlanService.createStudyPlan(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'DEAN')")
    public ResponseEntity<StudyPlanDto> updateStudyPlan(@PathVariable UUID id, @RequestBody CreateStudyPlanRequest request) {
        return ResponseEntity.ok(studyPlanService.updateStudyPlan(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteStudyPlan(@PathVariable UUID id) {
        studyPlanService.deleteStudyPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/disciplines")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'DEAN')")
    public ResponseEntity<StudyPlanDto> addDiscipline(@PathVariable UUID id, @RequestBody CreateStudyPlanDisciplineRequest request) {
        return ResponseEntity.ok(studyPlanService.addDiscipline(id, request));
    }

    @DeleteMapping("/{planId}/disciplines/{disciplineId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'DEAN')")
    public ResponseEntity<Void> removeDiscipline(@PathVariable UUID planId, @PathVariable UUID disciplineId) {
        studyPlanService.removeDiscipline(planId, disciplineId);
        return ResponseEntity.noContent().build();
    }
}
