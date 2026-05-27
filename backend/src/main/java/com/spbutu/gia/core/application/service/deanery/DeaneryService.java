package com.spbutu.gia.core.application.service.deanery;

import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.core.application.dto.deanery.*;
import com.spbutu.gia.core.domain.entity.Student;
import com.spbutu.gia.core.domain.entity.deanery.ContingentMovement;
import com.spbutu.gia.core.domain.entity.deanery.DeaneryOrder;
import com.spbutu.gia.core.domain.enums.MovementType;
import com.spbutu.gia.core.domain.enums.OrderStatus;
import com.spbutu.gia.core.domain.enums.OrderType;
import com.spbutu.gia.core.domain.repository.StudentRepository;
import com.spbutu.gia.core.domain.repository.deanery.ContingentMovementRepository;
import com.spbutu.gia.core.domain.repository.deanery.DeaneryOrderRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class DeaneryService {

    private final DeaneryOrderRepository orderRepository;
    private final ContingentMovementRepository movementRepository;
    private final StudentRepository studentRepository;
    private final AppUserRepository appUserRepository;

    public DeaneryService(DeaneryOrderRepository orderRepository,
                          ContingentMovementRepository movementRepository,
                          StudentRepository studentRepository,
                          AppUserRepository appUserRepository) {
        this.orderRepository = orderRepository;
        this.movementRepository = movementRepository;
        this.studentRepository = studentRepository;
        this.appUserRepository = appUserRepository;
    }

    // ========== ORDERS ==========

    @Transactional(readOnly = true)
    public List<DeaneryOrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DeaneryOrderDto getOrderById(UUID id) {
        return orderRepository.findById(id)
                .map(this::toOrderDto)
                .orElseThrow(() -> new IllegalArgumentException("Приказ не найден: " + id));
    }

    @Transactional
    public DeaneryOrderDto createOrder(CreateOrderRequest request) {
        DeaneryOrder order = new DeaneryOrder();
        order.setOrderNumber(request.getOrderNumber());
        order.setOrderDate(request.getOrderDate());
        order.setType(parseOrderType(request.getType()));
        order.setStatus(OrderStatus.DRAFT);
        order.setTitle(request.getTitle());
        order.setContent(request.getContent());
        order.setFilePath(request.getFilePath());

        String currentUsername = getCurrentUsername();
        if (currentUsername != null) {
            appUserRepository.findByUsername(currentUsername).ifPresent(order::setCreatedBy);
        }

        return toOrderDto(orderRepository.save(order));
    }

    @Transactional
    public DeaneryOrderDto updateOrder(UUID id, CreateOrderRequest request) {
        DeaneryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Приказ не найден: " + id));
        order.setOrderNumber(request.getOrderNumber());
        order.setOrderDate(request.getOrderDate());
        order.setType(parseOrderType(request.getType()));
        order.setTitle(request.getTitle());
        order.setContent(request.getContent());
        order.setFilePath(request.getFilePath());
        return toOrderDto(orderRepository.save(order));
    }

    @Transactional
    public DeaneryOrderDto approveOrder(UUID id) {
        DeaneryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Приказ не найден: " + id));
        order.setStatus(OrderStatus.APPROVED);
        order.setApprovedAt(LocalDateTime.now());
        String currentUsername = getCurrentUsername();
        if (currentUsername != null) {
            appUserRepository.findByUsername(currentUsername).ifPresent(order::setApprovedBy);
        }
        return toOrderDto(orderRepository.save(order));
    }

    @Transactional
    public DeaneryOrderDto signOrder(UUID id) {
        DeaneryOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Приказ не найден: " + id));
        order.setStatus(OrderStatus.SIGNED);
        return toOrderDto(orderRepository.save(order));
    }

    @Transactional
    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<DeaneryOrderDto> getOrdersByType(String type) {
        return orderRepository.findAllByType(parseOrderType(type)).stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    // ========== MOVEMENTS ==========

    @Transactional(readOnly = true)
    public List<ContingentMovementDto> getAllMovements() {
        return movementRepository.findAll().stream()
                .map(this::toMovementDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContingentMovementDto getMovementById(UUID id) {
        return movementRepository.findById(id)
                .map(this::toMovementDto)
                .orElseThrow(() -> new IllegalArgumentException("Запись движения не найдена: " + id));
    }

    @Transactional
    public ContingentMovementDto createMovement(CreateMovementRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));

        ContingentMovement movement = new ContingentMovement();
        movement.setStudent(student);
        movement.setMovementType(parseMovementType(request.getMovementType()));
        movement.setMovementDate(request.getMovementDate());
        movement.setReason(request.getReason());
        movement.setSemester(request.getSemester());
        movement.setAcademicYear(request.getAcademicYear());

        if (request.getOrderId() != null) {
            DeaneryOrder order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Приказ не найден"));
            movement.setOrder(order);
        }

        return toMovementDto(movementRepository.save(movement));
    }

    @Transactional
    public ContingentMovementDto updateMovement(UUID id, CreateMovementRequest request) {
        ContingentMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Запись движения не найдена: " + id));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Студент не найден"));
        movement.setStudent(student);
        movement.setMovementType(parseMovementType(request.getMovementType()));
        movement.setMovementDate(request.getMovementDate());
        movement.setReason(request.getReason());
        movement.setSemester(request.getSemester());
        movement.setAcademicYear(request.getAcademicYear());

        if (request.getOrderId() != null) {
            DeaneryOrder order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new IllegalArgumentException("Приказ не найден"));
            movement.setOrder(order);
        } else {
            movement.setOrder(null);
        }

        return toMovementDto(movementRepository.save(movement));
    }

    @Transactional
    public void deleteMovement(UUID id) {
        movementRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ContingentMovementDto> getMovementsByStudent(UUID studentId) {
        return movementRepository.findAllByStudentId(studentId).stream()
                .map(this::toMovementDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContingentMovementDto> getMovementsByType(String type) {
        return movementRepository.findAllByMovementType(parseMovementType(type)).stream()
                .map(this::toMovementDto)
                .collect(Collectors.toList());
    }

    // ========== MAPPING ==========

    private DeaneryOrderDto toOrderDto(DeaneryOrder order) {
        DeaneryOrderDto dto = new DeaneryOrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setOrderDate(order.getOrderDate());
        dto.setType(order.getType() != null ? order.getType().name() : null);
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setTitle(order.getTitle());
        dto.setContent(order.getContent());
        dto.setFilePath(order.getFilePath());
        dto.setCreatedByName(order.getCreatedBy() != null ? order.getCreatedBy().getFullName() : null);
        dto.setApprovedByName(order.getApprovedBy() != null ? order.getApprovedBy().getFullName() : null);
        dto.setApprovedAt(order.getApprovedAt());
        dto.setCreatedAt(order.getCreatedAt());
        return dto;
    }

    private ContingentMovementDto toMovementDto(ContingentMovement movement) {
        ContingentMovementDto dto = new ContingentMovementDto();
        dto.setId(movement.getId());
        if (movement.getStudent() != null) {
            dto.setStudentId(movement.getStudent().getId());
            dto.setStudentName(movement.getStudent().getLastName() + " " + movement.getStudent().getFirstName());
        }
        dto.setMovementType(movement.getMovementType() != null ? movement.getMovementType().name() : null);
        dto.setMovementDate(movement.getMovementDate());
        dto.setReason(movement.getReason());
        if (movement.getOrder() != null) {
            dto.setOrderId(movement.getOrder().getId());
            dto.setOrderNumber(movement.getOrder().getOrderNumber());
        }
        dto.setSemester(movement.getSemester());
        dto.setAcademicYear(movement.getAcademicYear());
        dto.setCreatedAt(movement.getCreatedAt());
        return dto;
    }

    private OrderType parseOrderType(String type) {
        if (type == null || type.isBlank()) return OrderType.OTHER;
        return OrderType.valueOf(type.toUpperCase());
    }

    private MovementType parseMovementType(String type) {
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Тип движения обязателен");
        return MovementType.valueOf(type.toUpperCase());
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }
}
