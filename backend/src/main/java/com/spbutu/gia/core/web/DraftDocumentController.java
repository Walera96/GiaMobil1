package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.DraftDocumentService;
import com.spbutu.gia.core.domain.entity.DraftDocument;
import com.spbutu.gia.core.domain.enums.DocumentType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST-контроллер для работы с черновиками документов.
 * Доступ только для ролей SECRETARY и DEAN.
 */
@RestController
@RequestMapping("/drafts")
@PreAuthorize("hasAnyRole('GEK_SECRETARY', 'DEAN')")
@SuppressWarnings("null")
public class DraftDocumentController {

    private final DraftDocumentService draftDocumentService;

    public DraftDocumentController(DraftDocumentService draftDocumentService) {
        this.draftDocumentService = draftDocumentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DraftDocument> getDraft(@PathVariable UUID id) {
        return ResponseEntity.ok(draftDocumentService.getDraft(id));
    }

    @GetMapping("/protocol/{protocolId}")
    public ResponseEntity<List<DraftDocument>> getDraftsByProtocol(@PathVariable UUID protocolId) {
        return ResponseEntity.ok(draftDocumentService.getDraftsByProtocol(protocolId));
    }

    @PostMapping
    public ResponseEntity<DraftDocument> createDraft(
            @RequestBody Map<String, String> request) {
        UUID protocolId = UUID.fromString(request.get("protocolId"));
        DocumentType documentType = DocumentType.valueOf(request.get("documentType"));
        String createdBy = request.get("createdBy");
        DraftDocument draft = draftDocumentService.createDraft(protocolId, documentType, createdBy);
        return ResponseEntity.ok(draft);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DraftDocument> updateDraft(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        DraftDocument draft = draftDocumentService.updateDraft(id, content);
        return ResponseEntity.ok(draft);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<DraftDocument> approveDraft(@PathVariable UUID id) {
        DraftDocument draft = draftDocumentService.approveDraft(id);
        return ResponseEntity.ok(draft);
    }

    @GetMapping(value = "/{id}/preview", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> previewDraft(@PathVariable UUID id) {
        String html = draftDocumentService.previewDraft(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                .body(html);
    }

    @GetMapping(value = "/{id}/docx", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadDocx(@PathVariable UUID id) {
        byte[] docxBytes = draftDocumentService.generateDocxFromDraft(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=draft_" + id + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(docxBytes);
    }
}
