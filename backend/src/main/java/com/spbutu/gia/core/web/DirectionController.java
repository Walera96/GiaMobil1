package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.DirectionService;
import com.spbutu.gia.core.domain.entity.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/directions")
public class DirectionController {

    private final DirectionService directionService;

    public DirectionController(DirectionService directionService) {
        this.directionService = directionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY')")
    public ResponseEntity<List<Direction>> getAll() {
        return ResponseEntity.ok(directionService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY')")
    public ResponseEntity<Direction> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(directionService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Direction> create(@RequestBody Direction direction) {
        return ResponseEntity.ok(directionService.create(direction));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Direction> update(@PathVariable UUID id, @RequestBody Direction direction) {
        return ResponseEntity.ok(directionService.update(id, direction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        directionService.delete(id);
        return ResponseEntity.ok().build();
    }
}
