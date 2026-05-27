package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.domain.entity.Gek;
import com.spbutu.gia.core.domain.repository.GekRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("null")
@Service
public class GekService {

    private final GekRepository gekRepository;

    public GekService(GekRepository gekRepository) {
        this.gekRepository = gekRepository;
    }

    @Transactional(readOnly = true)
    public List<Gek> getAll() {
        return gekRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Gek getById(UUID id) {
        return gekRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ГЭК не найден: " + id));
    }

    @Transactional
    public Gek create(Gek gek) {
        return gekRepository.save(gek);
    }

    @Transactional
    public Gek update(UUID id, Gek updated) {
        Gek gek = getById(id);
        gek.setName(updated.getName());
        return gekRepository.save(gek);
    }

    @Transactional
    public void delete(UUID id) {
        gekRepository.deleteById(id);
    }
}
