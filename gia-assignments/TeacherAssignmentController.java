package com.spbutu.gia.assignments.controller;

import com.spbutu.gia.assignments.domain.Assignment;
import com.spbutu.gia.assignments.domain.AssignmentSubmission;
import com.spbutu.gia.assignments.dto.*;
import com.spbutu.gia.assignments.service.AssignmentService;
import com.spbutu.gia.auth.domain.AppUser;
import com.spbutu.gia.auth.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/teacher/assignments")
@PreAuthorize("hasRole('SUPERVISOR') or hasRole('DEPARTMENT_HEAD')")
public class TeacherAssignmentController {

    private final AssignmentService assignmentService;

    public TeacherAssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(
            @Valid @RequestBody AssignmentCreateDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AppUser teacher = userDetails.getUser();
        Assignment created = assignmentService.createAssignment(dto, teacher);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/my")
    public ResponseEntity<Page<Assignment>> getMyAssignments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        Page<Assignment> assignments = assignmentService.getTeacherAssignments(
            userDetails.getUser().getId(), pageable);
        return ResponseEntity.ok(assignments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assignment> getAssignment(@PathVariable UUID id) {
        // TODO: проверить, что задание принадлежит текущему преподавателю
        return ResponseEntity.ok(/* assignmentService.getById(id) */);
    }

    @GetMapping("/{id}/submissions")
    public ResponseEntity<Page<AssignmentSubmission>> getSubmissions(
            @PathVariable UUID id,
            Pageable pageable) {
        Page<AssignmentSubmission> submissions = assignmentService.getSubmissions(id, pageable);
        return ResponseEntity.ok(submissions);
    }

    @PostMapping("/{id}/submissions/{subId}/review")
    public ResponseEntity<AssignmentSubmission> reviewSubmission(
            @PathVariable UUID id,
            @PathVariable UUID subId,
            @Valid @RequestBody ReviewDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AppUser teacher = userDetails.getUser();
        AssignmentSubmission reviewed = assignmentService.reviewSubmission(subId, dto, teacher);
        return ResponseEntity.ok(reviewed);
    }
}
