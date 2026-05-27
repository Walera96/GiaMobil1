package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.AdmissionCheckRequest;
import com.spbutu.gia.core.application.dto.AdmissionDto;
import com.spbutu.gia.core.application.service.AdmissionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер допусков к аттестации.
 */
@RestController
@RequestMapping("/admissions")
public class AdmissionController {

    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY')")
    public ResponseEntity<List<AdmissionDto>> getAllAdmissions() {
        return ResponseEntity.ok(admissionService.getAllAdmissions());
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'STUDENT')")
    public ResponseEntity<AdmissionDto> getAdmissionByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(admissionService.getAdmissionByStudentId(studentId));
    }

    @PostMapping("/check")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<AdmissionDto> checkAdmission(@RequestBody @Valid AdmissionCheckRequest request) {
        return ResponseEntity.ok(admissionService.checkAdmission(request));
    }
}
