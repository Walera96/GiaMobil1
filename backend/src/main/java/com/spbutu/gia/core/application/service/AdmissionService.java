package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.AdmissionCheckRequest;
import com.spbutu.gia.core.application.dto.AdmissionDto;
import com.spbutu.gia.core.domain.entity.Admission;
import com.spbutu.gia.core.domain.entity.Student;
import com.spbutu.gia.core.domain.repository.AdmissionRepository;
import com.spbutu.gia.core.domain.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис проверки допуска студентов к государственной итоговой аттестации.
 * Условия: баллы БРС ≥ 60, отсутствие задолженностей.
 */
@Service
public class AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final StudentRepository studentRepository;
    private final AuditService auditService;

    public AdmissionService(AdmissionRepository admissionRepository,
                            StudentRepository studentRepository,
                            AuditService auditService) {
        this.admissionRepository = admissionRepository;
        this.studentRepository = studentRepository;
        this.auditService = auditService;
    }

    /**
     * Проверяет и сохраняет результат допуска студента.
     *
     * @param request данные для проверки
     * @return DTO с результатом
     */
    @SuppressWarnings("null")
    @Transactional
    public AdmissionDto checkAdmission(AdmissionCheckRequest request) {
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден: " + request.studentId()));

        boolean isAdmitted = (request.brsScore() != null && request.brsScore() >= 60)
                && Boolean.FALSE.equals(request.hasDebt());

        Admission admission = admissionRepository.findByStudentId(request.studentId())
                .orElse(new Admission());

        String oldValue = admission.getId() != null
                ? "brsScore=" + admission.getBrsScore() + ", hasDebt=" + admission.getHasDebt()
                : null;

        admission.setStudent(student);
        admission.setBrsScore(request.brsScore());
        admission.setHasDebt(request.hasDebt());
        admission.setIsAdmitted(isAdmitted);
        admission.setCheckedAt(LocalDateTime.now());

        Admission saved = admissionRepository.save(admission);

        // Аудит изменения допуска
        auditService.logChange(
                "admission",
                saved.getId(),
                admission.getId() != null ? com.spbutu.gia.core.domain.enums.AuditAction.UPDATE
                        : com.spbutu.gia.core.domain.enums.AuditAction.INSERT,
                oldValue,
                "brsScore=" + request.brsScore() + ", hasDebt=" + request.hasDebt() + ", isAdmitted=" + isAdmitted,
                null,
                null
        );

        return toDto(saved);
    }

    /**
     * Получает все записи допусков.
     */
    @Transactional(readOnly = true)
    public List<AdmissionDto> getAllAdmissions() {
        return admissionRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Получает допуск студента по его ID.
     */
    @Transactional(readOnly = true)
    public AdmissionDto getAdmissionByStudentId(UUID studentId) {
        Admission admission = admissionRepository.findByStudentId(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Допуск не найден для студента: " + studentId));
        return toDto(admission);
    }

    private AdmissionDto toDto(Admission admission) {
        Student student = admission.getStudent();
        String fullName = null;
        String groupName = null;
        if (student != null) {
            fullName = student.getLastName() + " " + student.getFirstName();
            if (student.getMiddleName() != null) {
                fullName += " " + student.getMiddleName();
            }
            if (student.getGroup() != null) {
                groupName = student.getGroup().getName();
            }
        }
        return new AdmissionDto(
                admission.getId(),
                student != null ? student.getId() : null,
                fullName,
                groupName,
                admission.getBrsScore(),
                admission.getHasDebt(),
                admission.getIsAdmitted(),
                admission.getCheckedAt(),
                admission.getCreatedAt()
        );
    }
}
