package com.spbutu.gia.core.web;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.core.application.dto.ProtocolDto;
import com.spbutu.gia.core.application.dto.ProtocolRecordDto;
import com.spbutu.gia.core.application.dto.ScoreSheetDto;
import com.spbutu.gia.core.application.service.ProtocolService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер протоколов заседаний.
 */
@RestController
@RequestMapping("/protocols")
public class ProtocolController {

    private final ProtocolService protocolService;
    private final AppUserRepository appUserRepository;

    public ProtocolController(ProtocolService protocolService, AppUserRepository appUserRepository) {
        this.protocolService = protocolService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<ProtocolDto> getProtocol(@PathVariable UUID id) {
        return ResponseEntity.ok(protocolService.getProtocol(id));
    }

    @GetMapping("/meeting/{meetingId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<ProtocolDto> getProtocolByMeeting(@PathVariable UUID meetingId) {
        return ResponseEntity.ok(protocolService.getProtocolByMeetingId(meetingId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER', 'METHODIST')")
    public ResponseEntity<List<ProtocolDto>> searchProtocols(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID groupId,
            @RequestParam(required = false) UUID directionId,
            @RequestParam(required = false) String studentName) {
        return ResponseEntity.ok(protocolService.searchProtocols(studentId, groupId, directionId, studentName));
    }

    @GetMapping("/{id}/records")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<List<ProtocolRecordDto>> getProtocolRecords(@PathVariable UUID id) {
        return ResponseEntity.ok(protocolService.getProtocolRecords(id));
    }

    @PostMapping("/{id}/sign")
    @PreAuthorize("hasRole('GEK_SECRETARY')")
    public ResponseEntity<ProtocolDto> signProtocol(@PathVariable UUID id) {
        return ResponseEntity.ok(protocolService.signProtocol(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('GEK_CHAIRMAN')")
    public ResponseEntity<ProtocolDto> approveProtocol(@PathVariable UUID id) {
        UUID chairmanId = getCurrentUserId();
        return ResponseEntity.ok(protocolService.approveProtocol(id, chairmanId));
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

    @PostMapping("/meeting/{meetingId}/generate-records")
    @PreAuthorize("hasRole('GEK_SECRETARY')")
    public ResponseEntity<Void> generateRecords(@PathVariable UUID meetingId) {
        protocolService.generateProtocolRecords(meetingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/meeting/{meetingId}/score-sheet")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'METHODIST')")
    public ResponseEntity<ScoreSheetDto> getScoreSheet(@PathVariable UUID meetingId) {
        return ResponseEntity.ok(protocolService.buildScoreSheet(meetingId));
    }

    @GetMapping("/record/{recordId}/docx/individual")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN')")
    public ResponseEntity<byte[]> downloadIndividualDocx(@PathVariable UUID recordId) {
        byte[] docxBytes = protocolService.generateIndividualProtocolDocx(recordId);
        String studentName = protocolService.getStudentNameForRecord(recordId);
        String filename = (studentName != null && !studentName.isBlank())
                ? "protocol_" + studentName + "_" + recordId + ".docx"
                : "individual_protocol_" + recordId + ".docx";
        org.springframework.http.ContentDisposition disposition = org.springframework.http.ContentDisposition
                .attachment()
                .filename(filename, java.nio.charset.StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .header("Content-Disposition", disposition.toString())
                .body(docxBytes);
    }

    @GetMapping("/{id}/docx")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN')")
    public ResponseEntity<byte[]> downloadDocx(@PathVariable UUID id) {
        return downloadFinalDocx(id);
    }

    @GetMapping("/{id}/docx/final")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN')")
    public ResponseEntity<byte[]> downloadFinalDocx(@PathVariable UUID id) {
        byte[] docxBytes = protocolService.generateFinalProtocolDocx(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .header("Content-Disposition", "attachment; filename=final_protocol_" + id + ".docx")
                .body(docxBytes);
    }

    @GetMapping("/meeting/{meetingId}/docx/scoresheet")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'METHODIST')")
    public ResponseEntity<byte[]> downloadScoreSheetDocx(@PathVariable UUID meetingId) {
        byte[] docxBytes = protocolService.generateScoreSheetDocx(meetingId);
        var vedomost = protocolService.buildVedomostDto(meetingId);
        String directionCode = vedomost.getDirectionCode() != null ? vedomost.getDirectionCode().replace(".", "_") : "unknown";
        String groupName = vedomost.getGroupName() != null ? vedomost.getGroupName() : "unknown";
        String filename = directionCode + "_" + groupName + "_Ведомость.docx";
        org.springframework.http.ContentDisposition disposition = org.springframework.http.ContentDisposition
                .attachment()
                .filename(filename, java.nio.charset.StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
                .header("Content-Disposition", disposition.toString())
                .body(docxBytes);
    }

    // ========== PDF endpoints ==========

    @GetMapping("/record/{recordId}/pdf/individual")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN')")
    public ResponseEntity<byte[]> downloadIndividualPdf(@PathVariable UUID recordId) {
        byte[] pdfBytes = protocolService.generateIndividualProtocolPdf(recordId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=individual_protocol_" + recordId + ".pdf")
                .body(pdfBytes);
    }

    @GetMapping("/{id}/pdf/final")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN')")
    public ResponseEntity<byte[]> downloadFinalPdf(@PathVariable UUID id) {
        byte[] pdfBytes = protocolService.generateFinalProtocolPdf(id);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=final_protocol_" + id + ".pdf")
                .body(pdfBytes);
    }

    @GetMapping("/meeting/{meetingId}/pdf/scoresheet")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'GEK_SECRETARY', 'GEK_CHAIRMAN', 'METHODIST')")
    public ResponseEntity<byte[]> downloadScoreSheetPdf(@PathVariable UUID meetingId) {
        byte[] pdfBytes = protocolService.generateScoreSheetPdf(meetingId);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=scoresheet_" + meetingId + ".pdf")
                .body(pdfBytes);
    }
}
