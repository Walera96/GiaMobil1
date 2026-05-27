package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.domain.entity.StudyGroup;
import com.spbutu.gia.core.domain.repository.StudyGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("null")
@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;

    public StudyGroupService(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @Transactional(readOnly = true)
    public List<StudyGroup> getAll() {
        return studyGroupRepository.findAll();
    }

    @Transactional(readOnly = true)
    public StudyGroup getById(UUID id) {
        return studyGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Группа не найдена: " + id));
    }

    @Transactional
    public StudyGroup create(StudyGroup group) {
        return studyGroupRepository.save(group);
    }

    @Transactional
    public StudyGroup update(UUID id, StudyGroup updated) {
        StudyGroup group = getById(id);
        group.setName(updated.getName());
        group.setDirection(updated.getDirection());
        group.setCourse(updated.getCourse());
        return studyGroupRepository.save(group);
    }

    @Transactional
    public void delete(UUID id) {
        studyGroupRepository.deleteById(id);
    }
}
