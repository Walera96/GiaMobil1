package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.AuditLogDto;
import com.spbutu.gia.core.application.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер журнала аудита.
 */
@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditService auditService;

    public AuditLogController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogDto>> getAuditLogs(
            @RequestParam String table,
            @RequestParam UUID recordId) {
        return ResponseEntity.ok(auditService.getLogsByTableAndRecord(table, recordId));
    }
}
