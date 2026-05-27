package com.spbutu.gia.core.web.scos;

import com.spbutu.gia.core.application.dto.scos.*;
import com.spbutu.gia.core.application.service.scos.ScosExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/scos/export")
@SuppressWarnings("null")
public class ScosExportController {

    private final ScosExportService scosExportService;

    public ScosExportController(ScosExportService scosExportService) {
        this.scosExportService = scosExportService;
    }

    @GetMapping("/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'COMMISSION_MEMBER')")
    public ResponseEntity<ScosExportPackageDto> previewExport(
            @RequestParam(required = false) String directionId,
            @RequestParam(required = false) String academicYear) {
        ScosExportPackageDto dto = scosExportService.prepareExportData(
                directionId != null ? java.util.UUID.fromString(directionId) : null,
                academicYear);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/xml")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST')")
    public ResponseEntity<ByteArrayResource> exportXml(
            @RequestBody ScosExportRequestDto requestDto,
            @RequestParam(required = false) String createdBy) {
        ScosExportPackageDto dto = scosExportService.prepareExportData(
                requestDto.getDirectionId() != null ? java.util.UUID.fromString(requestDto.getDirectionId()) : null,
                requestDto.getAcademicYear());
        String xml = scosExportService.generateXml(dto);
        scosExportService.saveExportLog(dto, "xml", xml, createdBy);

        ByteArrayResource resource = new ByteArrayResource(xml.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gia_export.xml")
                .contentType(MediaType.APPLICATION_XML)
                .body(resource);
    }

    @PostMapping("/json")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST')")
    public ResponseEntity<ByteArrayResource> exportJson(
            @RequestBody ScosExportRequestDto requestDto,
            @RequestParam(required = false) String createdBy) {
        ScosExportPackageDto dto = scosExportService.prepareExportData(
                requestDto.getDirectionId() != null ? java.util.UUID.fromString(requestDto.getDirectionId()) : null,
                requestDto.getAcademicYear());
        String json = scosExportService.generateJson(dto);
        scosExportService.saveExportLog(dto, "json", json, createdBy);

        ByteArrayResource resource = new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gia_export.json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'COMMISSION_MEMBER')")
    public ResponseEntity<List<String>> validateExport(
            @RequestBody ScosExportRequestDto requestDto) {
        ScosExportPackageDto dto = scosExportService.prepareExportData(
                requestDto.getDirectionId() != null ? java.util.UUID.fromString(requestDto.getDirectionId()) : null,
                requestDto.getAcademicYear());
        return ResponseEntity.ok(scosExportService.validateExport(dto));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST')")
    public ResponseEntity<List<ScosExportLogDto>> getExportHistory() {
        return ResponseEntity.ok(scosExportService.getExportHistory());
    }

    @GetMapping("/config/{directionCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST')")
    public ResponseEntity<ScosExportConfigDto> getConfig(@PathVariable String directionCode) {
        return scosExportService.getConfig(directionCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScosExportConfigDto> saveConfig(@RequestBody ScosExportConfigDto configDto) {
        return ResponseEntity.ok(scosExportService.saveConfig(configDto));
    }
}
