package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.VedomostDto;
import com.spbutu.gia.core.infrastructure.docx.VedomostWordService;
import com.spbutu.gia.core.infrastructure.excel.VedomostExcelService;
import com.spbutu.gia.core.infrastructure.pdf.VedomostPdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

/**
 * REST-контроллер для генерации ведомости защиты ВКР в форматах PDF, Excel и Word.
 */
@RestController
@RequestMapping("/vedomost")
@SuppressWarnings("null")
public class VedomostController {

    private final VedomostPdfService pdfService;
    private final VedomostExcelService excelService;
    private final VedomostWordService wordService;

    public VedomostController(VedomostPdfService pdfService,
                              VedomostExcelService excelService,
                              VedomostWordService wordService) {
        this.pdfService = pdfService;
        this.excelService = excelService;
        this.wordService = wordService;
    }

    @PostMapping(value = "/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'METHODIST')")
    public ResponseEntity<byte[]> generatePdf(@RequestBody VedomostDto vedomost) {
        vedomost.calculateStatistics();
        byte[] pdfBytes = pdfService.generatePdf(vedomost);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=vedomost_" + vedomost.getDocumentNumber() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping(value = "/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'METHODIST')")
    public ResponseEntity<byte[]> generateExcel(@RequestBody VedomostDto vedomost) {
        vedomost.calculateStatistics();
        byte[] excelBytes = excelService.generateExcel(vedomost);
        String directionCode = vedomost.getDirectionCode() != null ? vedomost.getDirectionCode().replace(".", "_") : "unknown";
        String groupName = vedomost.getGroupName() != null ? vedomost.getGroupName() : "unknown";
        String filename = directionCode + "_" + groupName + "_Ведомость.xls";
        org.springframework.http.ContentDisposition disposition = org.springframework.http.ContentDisposition
                .attachment()
                .filename(filename, java.nio.charset.StandardCharsets.UTF_8)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    @PostMapping(value = "/word", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'METHODIST')")
    public ResponseEntity<byte[]> generateWord(@RequestBody VedomostDto vedomost) {
        vedomost.calculateStatistics();
        byte[] wordBytes = wordService.generateWord(vedomost);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=vedomost_" + vedomost.getDocumentNumber() + ".docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(wordBytes);
    }

    @GetMapping("/template")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'METHODIST')")
    public ResponseEntity<VedomostDto> getTemplate() {
        VedomostDto template = new VedomostDto();
        template.setDocumentNumber("319092");
        template.setAcademicYear("2025-2026");
        template.setDirectionCode("09.03.03");
        template.setDirectionName("Прикладная информатика");
        template.setDirectionShort("ПИ");
        template.setDepartment("Информационных технологий и математики");
        template.setGiaForm("Выполнение и защита выпускной квалификационной работы");
        template.setCourse(4);
        template.setGroupName("ОУИТб-ПИ01-22-4");
        template.setStudents(new ArrayList<>());
        template.setCommitteeMembers(new ArrayList<>());
        return ResponseEntity.ok(template);
    }
}
