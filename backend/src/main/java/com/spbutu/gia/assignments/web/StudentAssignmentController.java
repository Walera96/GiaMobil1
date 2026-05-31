package com.spbutu.gia.assignments.web;

import com.spbutu.gia.assignments.application.dto.AssignmentDto;
import com.spbutu.gia.assignments.application.dto.SubmissionDto;
import com.spbutu.gia.assignments.application.service.AssignmentService;
import com.spbutu.gia.assignments.application.service.AssignmentSubmissionService;
import com.spbutu.gia.auth.infrastructure.security.CustomUserDetails;
import com.spbutu.gia.assignments.domain.vo.AttachedFile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/student/assignments")
@PreAuthorize("hasAnyRole('STUDENT','SYSTEM_ADMIN','UNIVERSITY_ADMIN')")
public class StudentAssignmentController {

    private final AssignmentService assignmentService;
    private final AssignmentSubmissionService submissionService;

    public StudentAssignmentController(AssignmentService assignmentService,
                                       AssignmentSubmissionService submissionService) {
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    @GetMapping("/my")
    public ResponseEntity<List<AssignmentDto>> getMyAssignments(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(assignmentService.findByStudent(user.getId()));
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmissionDto> submit(@PathVariable UUID id,
                                                 @RequestBody Map<String, Object> body,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        @SuppressWarnings("unchecked")
        List<AttachedFile> files = (List<AttachedFile>) body.get("solutionFiles");
        String comment = (String) body.get("studentComment");
        return ResponseEntity.ok(submissionService.submit(id, user.getId(), files, comment));
    }

    @GetMapping("/submissions")
    public ResponseEntity<List<SubmissionDto>> getMySubmissions(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(submissionService.findByStudent(user.getId()));
    }
}
