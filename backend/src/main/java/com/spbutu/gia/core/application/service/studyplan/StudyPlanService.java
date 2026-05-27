package com.spbutu.gia.core.application.service.studyplan;

import com.spbutu.gia.core.application.dto.studyplan.*;
import com.spbutu.gia.core.domain.entity.Discipline;
import com.spbutu.gia.core.domain.entity.Direction;
import com.spbutu.gia.core.domain.entity.studyplan.StudyPlan;
import com.spbutu.gia.core.domain.entity.studyplan.StudyPlanDiscipline;
import com.spbutu.gia.core.domain.repository.DisciplineRepository;
import com.spbutu.gia.core.domain.repository.DirectionRepository;
import com.spbutu.gia.core.domain.repository.studyplan.StudyPlanDisciplineRepository;
import com.spbutu.gia.core.domain.repository.studyplan.StudyPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudyPlanDisciplineRepository planDisciplineRepository;
    private final DirectionRepository directionRepository;
    private final DisciplineRepository disciplineRepository;

    public StudyPlanService(StudyPlanRepository studyPlanRepository,
                            StudyPlanDisciplineRepository planDisciplineRepository,
                            DirectionRepository directionRepository,
                            DisciplineRepository disciplineRepository) {
        this.studyPlanRepository = studyPlanRepository;
        this.planDisciplineRepository = planDisciplineRepository;
        this.directionRepository = directionRepository;
        this.disciplineRepository = disciplineRepository;
    }

    @Transactional(readOnly = true)
    public List<StudyPlanDto> getAllStudyPlans() {
        return studyPlanRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudyPlanDto getStudyPlanById(UUID id) {
        return studyPlanRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Учебный план не найден: " + id));
    }

    @Transactional(readOnly = true)
    public List<StudyPlanDto> getStudyPlansByDirection(UUID directionId) {
        return studyPlanRepository.findAllByDirectionId(directionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudyPlanDto createStudyPlan(CreateStudyPlanRequest request) {
        StudyPlan plan = new StudyPlan();
        plan.setName(request.getName());
        plan.setProfile(request.getProfile());
        plan.setAcademicYear(request.getAcademicYear());
        plan.setFormOfStudy(request.getFormOfStudy());
        plan.setQualification(request.getQualification());
        plan.setTotalHours(request.getTotalHours());
        plan.setTotalCredits(request.getTotalCredits());
        plan.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");

        if (request.getDirectionId() != null) {
            Direction direction = directionRepository.findById(request.getDirectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Направление не найдено"));
            plan.setDirection(direction);
        }

        plan = studyPlanRepository.save(plan);

        if (request.getDisciplines() != null) {
            for (CreateStudyPlanDisciplineRequest dReq : request.getDisciplines()) {
                addDisciplineToPlan(plan, dReq);
            }
        }

        return toDto(studyPlanRepository.findById(plan.getId()).orElse(plan));
    }

    @Transactional
    public StudyPlanDto updateStudyPlan(UUID id, CreateStudyPlanRequest request) {
        StudyPlan plan = studyPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Учебный план не найден: " + id));

        plan.setName(request.getName());
        plan.setProfile(request.getProfile());
        plan.setAcademicYear(request.getAcademicYear());
        plan.setFormOfStudy(request.getFormOfStudy());
        plan.setQualification(request.getQualification());
        plan.setTotalHours(request.getTotalHours());
        plan.setTotalCredits(request.getTotalCredits());
        if (request.getStatus() != null) {
            plan.setStatus(request.getStatus());
        }

        if (request.getDirectionId() != null) {
            Direction direction = directionRepository.findById(request.getDirectionId())
                    .orElseThrow(() -> new IllegalArgumentException("Направление не найдено"));
            plan.setDirection(direction);
        }

        // Replace disciplines if provided
        if (request.getDisciplines() != null) {
            planDisciplineRepository.deleteAllByStudyPlanId(plan.getId());
            plan.getDisciplines().clear();
            for (CreateStudyPlanDisciplineRequest dReq : request.getDisciplines()) {
                addDisciplineToPlan(plan, dReq);
            }
        }

        return toDto(studyPlanRepository.save(plan));
    }

    @Transactional
    public void deleteStudyPlan(UUID id) {
        studyPlanRepository.deleteById(id);
    }

    @Transactional
    public StudyPlanDto addDiscipline(UUID planId, CreateStudyPlanDisciplineRequest request) {
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Учебный план не найден: " + planId));
        addDisciplineToPlan(plan, request);
        return toDto(studyPlanRepository.save(plan));
    }

    @Transactional
    public void removeDiscipline(UUID planId, UUID planDisciplineId) {
        StudyPlan plan = studyPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Учебный план не найден: " + planId));
        plan.getDisciplines().removeIf(pd -> pd.getId().equals(planDisciplineId));
        planDisciplineRepository.deleteById(planDisciplineId);
    }

    private void addDisciplineToPlan(StudyPlan plan, CreateStudyPlanDisciplineRequest request) {
        Discipline discipline = disciplineRepository.findById(request.getDisciplineId())
                .orElseThrow(() -> new IllegalArgumentException("Дисциплина не найдена: " + request.getDisciplineId()));

        StudyPlanDiscipline spd = new StudyPlanDiscipline();
        spd.setStudyPlan(plan);
        spd.setDiscipline(discipline);
        spd.setSemester(request.getSemester());
        spd.setCourse(request.getCourse());
        spd.setHours(request.getHours());
        spd.setCredits(request.getCredits());
        spd.setControlType(request.getControlType());
        spd.setIsMandatory(request.getIsMandatory() != null ? request.getIsMandatory() : true);
        plan.getDisciplines().add(spd);
    }

    private StudyPlanDto toDto(StudyPlan plan) {
        StudyPlanDto dto = new StudyPlanDto();
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        if (plan.getDirection() != null) {
            dto.setDirectionId(plan.getDirection().getId());
            dto.setDirectionCode(plan.getDirection().getCode());
            dto.setDirectionName(plan.getDirection().getName());
        }
        dto.setProfile(plan.getProfile());
        dto.setAcademicYear(plan.getAcademicYear());
        dto.setFormOfStudy(plan.getFormOfStudy());
        dto.setQualification(plan.getQualification());
        dto.setTotalHours(plan.getTotalHours());
        dto.setTotalCredits(plan.getTotalCredits());
        dto.setStatus(plan.getStatus());
        dto.setCreatedAt(plan.getCreatedAt());

        List<StudyPlanDisciplineDto> disciplineDtos = new ArrayList<>();
        if (plan.getDisciplines() != null) {
            for (StudyPlanDiscipline spd : plan.getDisciplines()) {
                StudyPlanDisciplineDto dDto = new StudyPlanDisciplineDto();
                dDto.setId(spd.getId());
                if (spd.getDiscipline() != null) {
                    dDto.setDisciplineId(spd.getDiscipline().getId());
                    dDto.setDisciplineName(spd.getDiscipline().getName());
                    dDto.setDisciplineCode(spd.getDiscipline().getCode());
                }
                dDto.setSemester(spd.getSemester());
                dDto.setCourse(spd.getCourse());
                dDto.setHours(spd.getHours());
                dDto.setCredits(spd.getCredits());
                dDto.setControlType(spd.getControlType());
                dDto.setIsMandatory(spd.getIsMandatory());
                disciplineDtos.add(dDto);
            }
        }
        dto.setDisciplines(disciplineDtos);
        return dto;
    }
}
