package com.spbutu.gia.core.web;

import com.spbutu.gia.core.domain.entity.Student;
import com.spbutu.gia.core.domain.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Контроллер для загрузки, скачивания и удаления файлов ВКР студентов.
 */
@RestController
@RequestMapping("/students")
@SuppressWarnings("null")
public class ThesisFileController {

    private static final Logger log = LoggerFactory.getLogger(ThesisFileController.class);
    private static final String UPLOAD_DIR = "uploads/thesis";

    private final StudentRepository studentRepository;

    public ThesisFileController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping("/{id}/thesis")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'STUDENT')")
    public ResponseEntity<Void> uploadThesis(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + id));

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.lastIndexOf('.') > 0) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String storedFilename = id + "_" + System.currentTimeMillis() + extension;
            Path targetPath = uploadPath.resolve(storedFilename);

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            student.setThesisFilePath(targetPath.toString());
            student.setThesisFileName(originalFilename);
            studentRepository.save(student);

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Ошибка загрузки файла ВКР для студента {}", id, e);
            throw new RuntimeException("Не удалось загрузить файл ВКР", e);
        }
    }

    @GetMapping("/{id}/thesis")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER', 'STUDENT')")
    public ResponseEntity<Resource> downloadThesis(@PathVariable UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + id));

        String filePath = student.getThesisFilePath();
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalStateException("Файл ВКР не загружен");
        }

        Path path = Paths.get(filePath);
        Resource resource = new FileSystemResource(path);
        if (!resource.exists()) {
            throw new IllegalStateException("Файл ВКР не найден на диске");
        }

        String filename = student.getThesisFileName() != null ? student.getThesisFileName() : "thesis.pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{id}/thesis")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'STUDENT')")
    public ResponseEntity<Void> deleteThesis(@PathVariable UUID id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + id));

        String filePath = student.getThesisFilePath();
        if (filePath != null && !filePath.isBlank()) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException e) {
                log.warn("Не удалось удалить файл ВКР: {}", filePath, e);
            }
        }

        student.setThesisFilePath(null);
        student.setThesisFileName(null);
        studentRepository.save(student);

        return ResponseEntity.ok().build();
    }
}
