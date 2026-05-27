package com.spbutu.gia.core.web;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.core.application.dto.CreateStatementRequest;
import com.spbutu.gia.core.application.dto.StatementDto;
import com.spbutu.gia.core.application.dto.StatementRecordDto;
import com.spbutu.gia.core.application.service.StatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/statements")
@SuppressWarnings("null")
public class StatementController {

    private final StatementService statementService;

    public StatementController(StatementService statementService) {
        this.statementService = statementService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY','CHAIRMAN')")
    public ResponseEntity<List<StatementDto>> getAll() {
        return ResponseEntity.ok(statementService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY','CHAIRMAN')")
    public ResponseEntity<StatementDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(statementService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY')")
    public ResponseEntity<StatementDto> create(@RequestBody CreateStatementRequest request,
                                                @AuthenticationPrincipal AppUser currentUser) {
        return ResponseEntity.ok(statementService.create(request, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY')")
    public ResponseEntity<StatementDto> update(@PathVariable UUID id, @RequestBody CreateStatementRequest request) {
        return ResponseEntity.ok(statementService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY','CHAIRMAN')")
    public ResponseEntity<StatementDto> changeStatus(@PathVariable UUID id, @RequestParam String status) {
        return ResponseEntity.ok(statementService.changeStatus(id, status));
    }

    @PutMapping("/{id}/records/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST','SECRETARY')")
    public ResponseEntity<StatementDto> updateRecord(@PathVariable UUID id,
                                                      @PathVariable UUID recordId,
                                                      @RequestBody StatementRecordDto recordDto) {
        return ResponseEntity.ok(statementService.updateRecord(id, recordId, recordDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        statementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
