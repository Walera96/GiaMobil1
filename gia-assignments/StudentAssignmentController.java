package com.spbutu.gia.assignments.controller;

import com.spbutu.gia.assignments.domain.Assignment;
import com.spbutu.gia.assignments.domain.AssignmentSubmission;
import com.spbutu.gia.assignments.dto.SubmissionDto;
import com.spbutu.gia.assignments.service.AssignmentService;
import com.spbutu.gia.auth.domain.AppUser;
import com.spbutu.gia.auth.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/student/assignments")
@PreAuthorize("hasRole('STUDENT')")
public class StudentAssignmentController {

    private final AssignmentService assignmentService;

    public StudentAssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping("/my")
    public ResponseEntity<Page<Assignment>> getMyAssignments(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        AppUser student = userDetails.getUser();
        // TODO: получить groupId из профиля студента
        UUID groupId = null;
        Page<Assignment> assignments = assignmentService.getStudentAssignments(
            student.getId(), groupId, pageable);
        return ResponseEntity.ok(assignments);
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<AssignmentSubmission> submitAssignment(
            @PathVariable UUID id,
            @Valid @RequestBody SubmissionDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AppUser student = userDetails.getUser();
        AssignmentSubmission submission = assignmentService.submitAssignment(
            id, student.getId(), dto);
        return ResponseEntity.ok(submission);
    }

    @GetMapping("/submissions")
    public ResponseEntity<Page<AssignmentSubmission>> getMySubmissions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable) {
        AppUser student = userDetails.getUser();
        Page<AssignmentSubmission> submissions = assignmentService.getStudentSubmissions(
            student.getId(), pageable);
        return ResponseEntity.ok(submissions);
    }
}
