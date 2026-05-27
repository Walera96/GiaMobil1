package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.dto.ActiveMeetingDto;
import com.spbutu.gia.core.application.dto.AgendaItemDto;
import com.spbutu.gia.core.application.dto.CreateMeetingRequest;
import com.spbutu.gia.core.application.dto.MeetingDto;
import com.spbutu.gia.core.application.service.MeetingService;
import com.spbutu.gia.core.domain.entity.AgendaItem;
import com.spbutu.gia.core.domain.repository.AgendaItemRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер заседаний ГЭК.
 */
@RestController
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final AgendaItemRepository agendaItemRepository;

    public MeetingController(MeetingService meetingService, AgendaItemRepository agendaItemRepository) {
        this.meetingService = meetingService;
        this.agendaItemRepository = agendaItemRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'METHODIST', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<List<MeetingDto>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'METHODIST', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<MeetingDto> getMeeting(@PathVariable UUID id) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('SECRETARY')")
    public ResponseEntity<MeetingDto> createMeeting(@RequestBody @Valid CreateMeetingRequest request) {
        // NOTE: в продакшене здесь нужно передать ID текущего пользователя из SecurityContext
        return ResponseEntity.ok(meetingService.createMeeting(request, null));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('SECRETARY')")
    public ResponseEntity<MeetingDto> activateMeeting(@PathVariable UUID id) {
        return ResponseEntity.ok(meetingService.activateMeeting(id));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('SECRETARY')")
    public ResponseEntity<MeetingDto> closeMeeting(@PathVariable UUID id) {
        return ResponseEntity.ok(meetingService.closeMeeting(id));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<ActiveMeetingDto> getActiveMeeting() {
        return ResponseEntity.ok(meetingService.findActiveMeeting());
    }

    @GetMapping("/{id}/agenda-items")
    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETARY', 'METHODIST', 'CHAIRMAN', 'GEK_MEMBER')")
    public ResponseEntity<List<AgendaItemDto>> getAgendaItems(@PathVariable UUID id) {
        List<AgendaItem> items = agendaItemRepository.findAllByMeetingId(id);
        List<AgendaItemDto> dtos = items.stream().map(this::toAgendaItemDto).toList();
        return ResponseEntity.ok(dtos);
    }

    private AgendaItemDto toAgendaItemDto(AgendaItem item) {
        var student = item.getStudent();
        return new AgendaItemDto(
                item.getId(),
                item.getMeeting() != null ? item.getMeeting().getId() : null,
                student != null ? student.getId() : null,
                student != null ? student.getLastName() + " " + student.getFirstName() : null,
                student != null ? student.getRecordBookNumber() : null,
                student != null ? student.getThesisTopic() : null,
                student != null ? student.getSupervisorName() : null,
                item.getPresentationDuration(),
                item.getAverageScore(),
                item.getOverallAverageScore(),
                null, // voteCount — можно добавить позже
                null,  // decision
                student != null ? student.getThesisFilePath() : null,
                student != null ? student.getThesisFileName() : null
        );
    }
}
