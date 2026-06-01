package com.spbutu.gia.assignments.application.service;

import com.spbutu.gia.assignments.application.dto.AssignmentCreateDto;
import com.spbutu.gia.assignments.application.dto.AssignmentDto;
import com.spbutu.gia.assignments.domain.entity.Assignment;
import com.spbutu.gia.assignments.domain.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    @Transactional
    public AssignmentDto create(AssignmentCreateDto dto, UUID teacherId) {
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.title());
        assignment.setDescription(dto.description());
        assignment.setAssignmentType(dto.assignmentType());
        assignment.setCreatedBy(teacherId);
        assignment.setTargetGroupId(dto.targetGroupId());
        assignment.setTargetStudentIds(dto.targetStudentIds());
        assignment.setDeadline(dto.deadline());
        assignment.setAllowLateSubmission(dto.allowLateSubmission());
        assignment.setMaxScore(dto.maxScore());
        assignment.setScoringConfig(dto.scoringConfig());
        assignment.setAttachedFiles(dto.attachedFiles());

        Assignment saved = assignmentRepository.save(assignment);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> findByTeacher(UUID teacherId) {
        return assignmentRepository.findAllByCreatedByOrderByCreatedAtDesc(teacherId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> findByGroup(UUID groupId) {
        return assignmentRepository.findAllByTargetGroupId(groupId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentDto> findByStudent(UUID studentId) {
        Objects.requireNonNull(studentId, "studentId must not be null");
        return assignmentRepository.findAllByTargetStudentIdsContains(studentId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AssignmentDto findById(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задание не найдено"));
        return toDto(assignment);
    }

    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "id must not be null");
        assignmentRepository.deleteById(id);
    }

    private AssignmentDto toDto(Assignment a) {
        return new AssignmentDto(
                a.getId(),
                a.getTitle(),
                a.getDescription(),
                a.getAssignmentType(),
                a.getCreatedBy(),
                null, // createdByName — можно дополнить запросом к AppUser
                a.getTargetGroupId(),
                null, // targetGroupName
                a.getTargetStudentIds(),
                a.getDeadline(),
                a.isAllowLateSubmission(),
                a.getMaxScore(),
                a.getScoringConfig(),
                a.getAttachedFiles(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
