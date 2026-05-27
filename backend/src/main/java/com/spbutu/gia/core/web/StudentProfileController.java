package com.spbutu.gia.core.web;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.core.application.dto.AdmissionDto;
import com.spbutu.gia.core.application.dto.StudentGradeDto;
import com.spbutu.gia.core.application.dto.StudentMeetingInfoDto;
import com.spbutu.gia.core.application.dto.StudentProfileDto;
import com.spbutu.gia.core.application.dto.UpdateStudentProfileRequest;
import com.spbutu.gia.core.application.dto.deanery.ContingentMovementDto;
import com.spbutu.gia.core.application.dto.student.*;
import com.spbutu.gia.core.application.service.StudentProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер личного кабинета студента (расширенный).
 * Доступ только для роли STUDENT.
 */
@RestController
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;
    private final AppUserRepository appUserRepository;

    public StudentProfileController(StudentProfileService studentProfileService,
                                    AppUserRepository appUserRepository) {
        this.studentProfileService = studentProfileService;
        this.appUserRepository = appUserRepository;
    }

    // ========== LEGACY PROFILE ==========

    @GetMapping("/profile")
    public ResponseEntity<StudentProfileDto> getProfile() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentProfileDto> updateProfile(@RequestBody UpdateStudentProfileRequest request) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.updateProfile(userId, request.thesisTopic(), request.supervisorName()));
    }

    @PostMapping("/thesis")
    public ResponseEntity<Void> uploadThesis(@RequestParam("file") MultipartFile file) {
        UUID userId = getCurrentUserId();
        studentProfileService.uploadThesis(userId, file);
        return ResponseEntity.ok().build();
    }

    // ========== GRADES / ADMISSION / MEETING ==========

    @GetMapping("/grades")
    public ResponseEntity<List<StudentGradeDto>> getGrades() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getGrades(userId));
    }

    @GetMapping("/admission")
    public ResponseEntity<AdmissionDto> getAdmission() {
        UUID userId = getCurrentUserId();
        AdmissionDto dto = studentProfileService.getAdmission(userId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/meeting-info")
    public ResponseEntity<StudentMeetingInfoDto> getMeetingInfo() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getMeetingInfo(userId));
    }

    // ========== EXTENDED PROFILE ==========

    @GetMapping("/extended-profile")
    public ResponseEntity<StudentExtendedProfileDto> getExtendedProfile() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getExtendedProfile(userId));
    }

    @PutMapping("/extended-profile")
    public ResponseEntity<StudentExtendedProfileDto> updateExtendedProfile(@RequestBody UpdateExtendedProfileRequest request) {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.updateExtendedProfile(userId, request));
    }

    // ========== DASHBOARD ==========

    @GetMapping("/dashboard")
    public ResponseEntity<StudentDashboardDto> getDashboard() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getDashboard(userId));
    }

    // ========== NOTIFICATIONS ==========

    @GetMapping("/notifications")
    public ResponseEntity<List<StudentNotificationDto>> getNotifications() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getNotifications(userId));
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationRead(@PathVariable UUID id) {
        studentProfileService.markNotificationRead(id);
        return ResponseEntity.ok().build();
    }

    // ========== MOVEMENTS ==========

    @GetMapping("/movements")
    public ResponseEntity<List<ContingentMovementDto>> getMovements() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getStudentMovements(userId));
    }

    // ========== PROTOCOLS ==========

    @GetMapping("/protocols")
    public ResponseEntity<List<StudentProtocolDto>> getProtocols() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(studentProfileService.getStudentProtocols(userId));
    }

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails userDetails) {
            username = userDetails.getUsername();
        } else {
            username = principal.toString();
        }
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + username));
        return appUser.getId();
    }
}
