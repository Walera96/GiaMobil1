package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.*;
import com.spbutu.gia.core.application.dto.deanery.ContingentMovementDto;
import com.spbutu.gia.core.application.dto.student.*;
import com.spbutu.gia.core.domain.entity.Grade;
import com.spbutu.gia.core.domain.entity.Notification;
import com.spbutu.gia.core.domain.entity.Student;
import com.spbutu.gia.core.domain.entity.deanery.ContingentMovement;
import com.spbutu.gia.core.domain.repository.*;
import com.spbutu.gia.core.domain.repository.deanery.ContingentMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис личного кабинета студента (расширенный).
 */
@Service
public class StudentProfileService {

    private static final Logger log = LoggerFactory.getLogger(StudentProfileService.class);
    private static final String UPLOAD_DIR = "uploads/thesis";

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final AdmissionRepository admissionRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final NotificationRepository notificationRepository;
    private final ContingentMovementRepository contingentMovementRepository;
    private final ProtocolRecordRepository protocolRecordRepository;

    public StudentProfileService(StudentRepository studentRepository,
                                 GradeRepository gradeRepository,
                                 AdmissionRepository admissionRepository,
                                 AgendaItemRepository agendaItemRepository,
                                 NotificationRepository notificationRepository,
                                 ContingentMovementRepository contingentMovementRepository,
                                 ProtocolRecordRepository protocolRecordRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.admissionRepository = admissionRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.notificationRepository = notificationRepository;
        this.contingentMovementRepository = contingentMovementRepository;
        this.protocolRecordRepository = protocolRecordRepository;
    }

    // ========== LEGACY METHODS (backward compatible) ==========

    @Transactional
    @SuppressWarnings("null")
    public StudentProfileDto updateProfile(UUID userId, String thesisTopic, String supervisorName) {
        Student student = findStudentByUserId(userId);
        if (thesisTopic != null) {
            student.setThesisTopic(thesisTopic);
        }
        if (supervisorName != null) {
            student.setSupervisorName(supervisorName);
        }
        studentRepository.save(student);
        return getProfile(userId);
    }

    @Transactional(readOnly = true)
    public StudentProfileDto getProfile(UUID userId) {
        Student student = findStudentByUserId(userId);
        var group = student.getGroup();
        var direction = group != null ? group.getDirection() : null;
        Double avg = calculateAverageGrade(student.getId());

        return new StudentProfileDto(
                student.getId(),
                student.getLastName() + " " + student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() : ""),
                student.getRecordBookNumber(),
                group != null ? group.getName() : null,
                direction != null ? direction.getCode() : null,
                direction != null ? direction.getName() : null,
                student.getThesisTopic(),
                student.getSupervisorName(),
                student.getThesisFilePath(),
                student.getThesisFileName(),
                avg
        );
    }

    @Transactional(readOnly = true)
    public List<StudentGradeDto> getGrades(UUID userId) {
        Student student = findStudentByUserId(userId);
        return gradeRepository.findAllByStudentId(student.getId())
                .stream()
                .map(this::toGradeDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdmissionDto getAdmission(UUID userId) {
        Student student = findStudentByUserId(userId);
        var admission = admissionRepository.findByStudentId(student.getId()).orElse(null);
        if (admission == null) {
            return null;
        }
        var group = student.getGroup();
        return new AdmissionDto(
                admission.getId(),
                student.getId(),
                student.getLastName() + " " + student.getFirstName(),
                group != null ? group.getName() : null,
                admission.getBrsScore(),
                admission.getHasDebt(),
                admission.getIsAdmitted(),
                admission.getCheckedAt(),
                admission.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public StudentMeetingInfoDto getMeetingInfo(UUID userId) {
        Student student = findStudentByUserId(userId);
        var agendaItems = agendaItemRepository.findAllByStudentId(student.getId());
        if (agendaItems.isEmpty()) {
            return null;
        }
        var item = agendaItems.get(0);
        var meeting = item.getMeeting();
        if (meeting == null) {
            return null;
        }
        var gek = meeting.getGek();
        return new StudentMeetingInfoDto(
                meeting.getId(),
                meeting.getMeetingDate(),
                meeting.getStartTime() != null ? meeting.getStartTime().toString() : null,
                meeting.getEndTime() != null ? meeting.getEndTime().toString() : null,
                meeting.getLocation(),
                gek != null ? gek.getName() : null,
                item.getOrderNumber()
        );
    }

    @Transactional
    public void uploadThesis(UUID userId, MultipartFile file) {
        Student student = findStudentByUserId(userId);
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
            String storedFilename = student.getId() + "_" + System.currentTimeMillis() + extension;
            Path targetPath = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            student.setThesisFilePath(targetPath.toString());
            student.setThesisFileName(originalFilename);
            studentRepository.save(student);
        } catch (IOException e) {
            log.error("Ошибка загрузки файла ВКР для студента {}", student.getId(), e);
            throw new RuntimeException("Не удалось загрузить файл ВКР", e);
        }
    }

    @Transactional(readOnly = true)
    public Double calculateAverageGrade(UUID studentId) {
        List<Grade> grades = gradeRepository.findAllByStudentId(studentId);
        if (grades.isEmpty()) {
            return null;
        }
        double avg = grades.stream()
                .mapToInt(Grade::getScore)
                .average()
                .orElse(0.0);
        return Math.round(avg * 100.0) / 100.0;
    }

    // ========== EXTENDED PROFILE ==========

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public StudentExtendedProfileDto getExtendedProfile(UUID userId) {
        Student student = findStudentByUserId(userId);
        var group = student.getGroup();
        var direction = group != null ? group.getDirection() : null;
        Double avg = calculateAverageGrade(student.getId());

        StudentExtendedProfileDto dto = new StudentExtendedProfileDto();
        dto.setId(student.getId());
        dto.setFullName(student.getLastName() + " " + student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() : ""));
        dto.setEmail(student.getUser() != null ? student.getUser().getEmail() : null);
        dto.setPhone(student.getPhone());
        dto.setRecordBookNumber(student.getRecordBookNumber());
        dto.setGroupName(group != null ? group.getName() : null);
        dto.setDirectionCode(direction != null ? direction.getCode() : null);
        dto.setDirectionName(direction != null ? direction.getName() : null);
        dto.setCourse(group != null ? String.valueOf(group.getCourse()) : null);
        dto.setThesisTopic(student.getThesisTopic());
        dto.setSupervisorName(student.getSupervisorName());
        dto.setThesisFilePath(student.getThesisFilePath());
        dto.setThesisFileName(student.getThesisFileName());
        dto.setPhotoUrl(student.getPhotoUrl());
        dto.setAboutMe(student.getAboutMe());
        dto.setAverageGrade(avg);
        return dto;
    }

    @Transactional
    @SuppressWarnings("null")
    public StudentExtendedProfileDto updateExtendedProfile(UUID userId, UpdateExtendedProfileRequest request) {
        Student student = findStudentByUserId(userId);
        if (request.getThesisTopic() != null) {
            student.setThesisTopic(request.getThesisTopic());
        }
        if (request.getSupervisorName() != null) {
            student.setSupervisorName(request.getSupervisorName());
        }
        if (request.getPhone() != null) {
            student.setPhone(request.getPhone());
        }
        if (request.getAboutMe() != null) {
            student.setAboutMe(request.getAboutMe());
        }
        if (request.getPhotoUrl() != null) {
            student.setPhotoUrl(request.getPhotoUrl());
        }
        studentRepository.save(student);
        return getExtendedProfile(userId);
    }

    // ========== DASHBOARD ==========

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public StudentDashboardDto getDashboard(UUID userId) {
        Student student = findStudentByUserId(userId);
        StudentDashboardDto dashboard = new StudentDashboardDto();

        dashboard.setProfile(getExtendedProfile(userId));
        dashboard.setAdmission(getAdmission(userId));
        dashboard.setMeetingInfo(getMeetingInfo(userId));
        dashboard.setGrades(getGrades(userId));
        dashboard.setNotifications(getNotifications(userId));
        dashboard.setUnreadNotificationsCount((int) notificationRepository.countByUserIdAndReadFalse(userId));

        // Defense status
        var agendaItems = agendaItemRepository.findAllByStudentId(student.getId());
        var protocolRecords = protocolRecordRepository.findByStudentId(student.getId());
        String defenseStatus = "PENDING";
        if (!protocolRecords.isEmpty()) {
            Integer finalScore = protocolRecords.get(0).getFinalScore();
            if (finalScore != null && finalScore >= 3) {
                defenseStatus = "COMPLETED";
            } else {
                defenseStatus = "NOT_DEFENDED";
            }
        } else if (!agendaItems.isEmpty()) {
            defenseStatus = "SCHEDULED";
        }
        dashboard.setDefenseStatus(defenseStatus);

        // Diploma status from contingent movement
        var movements = contingentMovementRepository.findAllByStudentId(student.getId());
        boolean diplomaIssued = movements.stream().anyMatch(m -> m.getMovementType() != null && m.getMovementType().name().equals("DIPLOMA_ISSUED"));
        dashboard.setDiplomaStatus(diplomaIssued ? "ISSUED" : "NOT_ISSUED");

        // Overall progress: admitted = 30%, has grades = +20%, has meeting = +20%, defended = +30%
        double progress = 0;
        var admission = admissionRepository.findByStudentId(student.getId()).orElse(null);
        if (admission != null && Boolean.TRUE.equals(admission.getIsAdmitted())) progress += 30;
        if (!gradeRepository.findAllByStudentId(student.getId()).isEmpty()) progress += 20;
        if (!agendaItems.isEmpty()) progress += 20;
        if ("COMPLETED".equals(defenseStatus)) progress += 30;
        dashboard.setOverallProgress(progress);

        return dashboard;
    }

    // ========== NOTIFICATIONS ==========

    @Transactional(readOnly = true)
    public List<StudentNotificationDto> getNotifications(UUID userId) {
        return notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toNotificationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @SuppressWarnings("null")
    public void markNotificationRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Уведомление не найдено"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    // ========== MOVEMENTS ==========

    @Transactional(readOnly = true)
    public List<ContingentMovementDto> getStudentMovements(UUID userId) {
        Student student = findStudentByUserId(userId);
        return contingentMovementRepository.findAllByStudentId(student.getId()).stream()
                .map(this::toMovementDto)
                .collect(Collectors.toList());
    }

    // ========== PROTOCOLS ==========

    @Transactional(readOnly = true)
    public List<StudentProtocolDto> getStudentProtocols(UUID userId) {
        Student student = findStudentByUserId(userId);
        return protocolRecordRepository.findByStudentId(student.getId()).stream()
                .map(pr -> {
                    StudentProtocolDto dto = new StudentProtocolDto();
                    dto.setId(pr.getId());
                    dto.setFinalScore(pr.getFinalScore());
                    dto.setDecision(pr.getDecision());
                    dto.setProtocolNumber(pr.getProtocol() != null ? pr.getProtocol().getProtocolNumber() : null);
                    dto.setProtocolDate(pr.getProtocol() != null && pr.getProtocol().getGeneratedAt() != null ? pr.getProtocol().getGeneratedAt().toLocalDate().toString() : null);
                    dto.setStatus(pr.getProtocol() != null && pr.getProtocol().getStatus() != null ? pr.getProtocol().getStatus().name() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ========== PRIVATE HELPERS ==========

    private Student findStudentByUserId(UUID userId) {
        return studentRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден для пользователя: " + userId));
    }

    private StudentGradeDto toGradeDto(Grade grade) {
        return new StudentGradeDto(
                grade.getId(),
                grade.getSubjectName(),
                grade.getScore(),
                grade.getSemester()
        );
    }

    private StudentNotificationDto toNotificationDto(Notification n) {
        StudentNotificationDto dto = new StudentNotificationDto();
        dto.setId(n.getId());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setType(n.getType());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }

    private ContingentMovementDto toMovementDto(ContingentMovement m) {
        ContingentMovementDto dto = new ContingentMovementDto();
        dto.setId(m.getId());
        if (m.getStudent() != null) {
            dto.setStudentId(m.getStudent().getId());
            dto.setStudentName(m.getStudent().getLastName() + " " + m.getStudent().getFirstName());
        }
        dto.setMovementType(m.getMovementType() != null ? m.getMovementType().name() : null);
        dto.setMovementDate(m.getMovementDate());
        dto.setReason(m.getReason());
        if (m.getOrder() != null) {
            dto.setOrderId(m.getOrder().getId());
            dto.setOrderNumber(m.getOrder().getOrderNumber());
        }
        dto.setSemester(m.getSemester());
        dto.setAcademicYear(m.getAcademicYear());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }
}
