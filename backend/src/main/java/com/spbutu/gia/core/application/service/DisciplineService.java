package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.CreateDisciplineRequest;
import com.spbutu.gia.core.application.dto.DisciplineDto;
import com.spbutu.gia.core.domain.entity.Discipline;
import com.spbutu.gia.core.domain.entity.Direction;
import com.spbutu.gia.core.domain.repository.DisciplineRepository;
import com.spbutu.gia.core.domain.repository.DirectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class DisciplineService {

    private final DisciplineRepository disciplineRepository;
    private final DirectionRepository directionRepository;

    public DisciplineService(DisciplineRepository disciplineRepository, DirectionRepository directionRepository) {
        this.disciplineRepository = disciplineRepository;
        this.directionRepository = directionRepository;
    }

    @Transactional(readOnly = true)
    public List<DisciplineDto> findAll() {
        return disciplineRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DisciplineDto findById(UUID id) {
        return disciplineRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Дисциплина не найдена"));
    }

    @Transactional
    public DisciplineDto create(CreateDisciplineRequest request) {
        Discipline discipline = new Discipline();
        discipline.setCode(request.getCode());
        discipline.setName(request.getName());
        discipline.setHours(request.getHours());
        discipline.setEctsCredits(request.getEctsCredits());
        discipline.setCourse(request.getCourse());
        discipline.setSemester(request.getSemester());
        discipline.setControlType(request.getControlType());
        if (request.getDirectionId() != null) {
            Direction direction = directionRepository.findById(request.getDirectionId())
                    .orElseThrow(() -> new RuntimeException("Направление не найдено"));
            discipline.setDirection(direction);
        }
        return toDto(disciplineRepository.save(discipline));
    }

    @Transactional
    public DisciplineDto update(UUID id, CreateDisciplineRequest request) {
        Discipline discipline = disciplineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Дисциплина не найдена"));
        discipline.setCode(request.getCode());
        discipline.setName(request.getName());
        discipline.setHours(request.getHours());
        discipline.setEctsCredits(request.getEctsCredits());
        discipline.setCourse(request.getCourse());
        discipline.setSemester(request.getSemester());
        discipline.setControlType(request.getControlType());
        if (request.getDirectionId() != null) {
            Direction direction = directionRepository.findById(request.getDirectionId())
                    .orElseThrow(() -> new RuntimeException("Направление не найдено"));
            discipline.setDirection(direction);
        }
        return toDto(disciplineRepository.save(discipline));
    }

    @Transactional
    public void delete(UUID id) {
        disciplineRepository.deleteById(id);
    }

    private DisciplineDto toDto(Discipline discipline) {
        DisciplineDto dto = new DisciplineDto();
        dto.setId(discipline.getId());
        dto.setCode(discipline.getCode());
        dto.setName(discipline.getName());
        dto.setHours(discipline.getHours());
        dto.setEctsCredits(discipline.getEctsCredits());
        dto.setCourse(discipline.getCourse());
        dto.setSemester(discipline.getSemester());
        dto.setControlType(discipline.getControlType());
        if (discipline.getDirection() != null) {
            dto.setDirectionId(discipline.getDirection().getId());
            dto.setDirectionName(discipline.getDirection().getName());
        }
        return dto;
    }
}
