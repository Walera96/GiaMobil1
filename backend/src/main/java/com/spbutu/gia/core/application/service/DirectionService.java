package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.domain.entity.Direction;
import com.spbutu.gia.core.domain.repository.DirectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("null")
@Service
public class DirectionService {

    private final DirectionRepository directionRepository;

    public DirectionService(DirectionRepository directionRepository) {
        this.directionRepository = directionRepository;
    }

    @Transactional(readOnly = true)
    public List<Direction> getAll() {
        return directionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Direction getById(UUID id) {
        return directionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Направление не найдено: " + id));
    }

    @Transactional
    public Direction create(Direction direction) {
        return directionRepository.save(direction);
    }

    @Transactional
    public Direction update(UUID id, Direction updated) {
        Direction direction = getById(id);
        direction.setCode(updated.getCode());
        direction.setName(updated.getName());
        return directionRepository.save(direction);
    }

    @Transactional
    public void delete(UUID id) {
        directionRepository.deleteById(id);
    }
}
