package com.spbutu.gia.core.web.deanery;

import com.spbutu.gia.core.application.dto.deanery.*;
import com.spbutu.gia.core.application.service.deanery.DeaneryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deanery")
@SuppressWarnings("null")
public class DeaneryController {

    private final DeaneryService deaneryService;

    public DeaneryController(DeaneryService deaneryService) {
        this.deaneryService = deaneryService;
    }

    // ========== ORDERS ==========

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<List<DeaneryOrderDto>> getAllOrders() {
        return ResponseEntity.ok(deaneryService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<DeaneryOrderDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(deaneryService.getOrderById(id));
    }

    @GetMapping("/orders/by-type")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<List<DeaneryOrderDto>> getOrdersByType(@RequestParam String type) {
        return ResponseEntity.ok(deaneryService.getOrdersByType(type));
    }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<DeaneryOrderDto> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(deaneryService.createOrder(request));
    }

    @PutMapping("/orders/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<DeaneryOrderDto> updateOrder(@PathVariable UUID id, @RequestBody CreateOrderRequest request) {
        return ResponseEntity.ok(deaneryService.updateOrder(id, request));
    }

    @PostMapping("/orders/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    public ResponseEntity<DeaneryOrderDto> approveOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(deaneryService.approveOrder(id));
    }

    @PostMapping("/orders/{id}/sign")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEAN')")
    public ResponseEntity<DeaneryOrderDto> signOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(deaneryService.signOrder(id));
    }

    @DeleteMapping("/orders/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        deaneryService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    // ========== MOVEMENTS ==========

    @GetMapping("/movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<List<ContingentMovementDto>> getAllMovements() {
        return ResponseEntity.ok(deaneryService.getAllMovements());
    }

    @GetMapping("/movements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<ContingentMovementDto> getMovementById(@PathVariable UUID id) {
        return ResponseEntity.ok(deaneryService.getMovementById(id));
    }

    @GetMapping("/movements/by-student")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN', 'STUDENT')")
    public ResponseEntity<List<ContingentMovementDto>> getMovementsByStudent(@RequestParam UUID studentId) {
        return ResponseEntity.ok(deaneryService.getMovementsByStudent(studentId));
    }

    @GetMapping("/movements/by-type")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<List<ContingentMovementDto>> getMovementsByType(@RequestParam String type) {
        return ResponseEntity.ok(deaneryService.getMovementsByType(type));
    }

    @PostMapping("/movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<ContingentMovementDto> createMovement(@RequestBody CreateMovementRequest request) {
        return ResponseEntity.ok(deaneryService.createMovement(request));
    }

    @PutMapping("/movements/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'DEAN')")
    public ResponseEntity<ContingentMovementDto> updateMovement(@PathVariable UUID id, @RequestBody CreateMovementRequest request) {
        return ResponseEntity.ok(deaneryService.updateMovement(id, request));
    }

    @DeleteMapping("/movements/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovement(@PathVariable UUID id) {
        deaneryService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }
}
