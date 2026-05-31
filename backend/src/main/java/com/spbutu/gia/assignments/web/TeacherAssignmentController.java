package com.spbutu.gia.assignments.web;

import com.spbutu.gia.assignments.application.dto.AssignmentCreateDto;
import com.spbutu.gia.assignments.application.dto.AssignmentDto;
import com.spbutu.gia.assignments.application.dto.ReviewDto;
import com.spbutu.gia.assignments.application.dto.SubmissionDto;
import com.spbutu.gia.assignments.application.service.AssignmentService;
import com.spbutu.gia.assignments.application.service.AssignmentSubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import com.spbutu.gia.auth.infrastructure.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teacher/assignments")
@PreAuthorize("hasAnyRole('SUPERVISOR','DEPARTMENT_HEAD','SYSTEM_ADMIN','UNIVERSITY_ADMIN')")
public class TeacherAssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentSubmissionService submissionService;

    public TeacherAssignmentController(AssignmentService assignmentService,
                                       AssignmentSubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<AssignmentDto> create(@Valid @RequestBody AssignmentCreateDto dto,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(assignmentService.create(dto, user.getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AssignmentDto>> getMyAssignments(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(assignmentService.findByTeacher(user.getId()));
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<List<SubmissionDto>> getSubmissions(@PathVariable UUID id) {
        return ResponseEntity.ok(submissionService.findByAssignment(id));
    }

    @PostMapping("/{id}/submissions/{subId}/review")
    public ResponseEntity<SubmissionDto> review(@PathVariable UUID id,
                                                 @PathVariable UUID subId,
                                                 @Valid @RequestBody ReviewDto dto,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(submissionService.review(subId, dto, user.getId()));
    }
}
