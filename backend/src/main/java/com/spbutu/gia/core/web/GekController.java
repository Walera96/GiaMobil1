package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.GekService;
import com.spbutu.gia.core.domain.entity.Gek;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/geks")
public class GekController {

    private final GekService gekService;

    public GekController(GekService gekService) {
        this.gekService = gekService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY')")
    public ResponseEntity<List<Gek>> getAll() {
        return ResponseEntity.ok(gekService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'METHODIST', 'GEK_SECRETARY')")
    public ResponseEntity<Gek> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(gekService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Gek> create(@RequestBody Gek gek) {
        return ResponseEntity.ok(gekService.create(gek));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Gek> update(@PathVariable UUID id, @RequestBody Gek gek) {
        return ResponseEntity.ok(gekService.update(id, gek));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('METHODIST')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        gekService.delete(id);
        return ResponseEntity.ok().build();
    }
}
