package com.spbutu.gia.core.web.schedule;

import com.spbutu.gia.core.application.dto.schedule.DefenseSchedulePreviewDto;
import com.spbutu.gia.core.application.dto.schedule.DefenseScheduleRequestDto;
import com.spbutu.gia.core.application.dto.schedule.DefenseDayDto;
import com.spbutu.gia.core.application.service.schedule.DefenseScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schedule")
@SuppressWarnings("null")
public class DefenseScheduleController {

    private final DefenseScheduleService scheduleService;

    public DefenseScheduleController(DefenseScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/preview")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY')")
    public ResponseEntity<DefenseSchedulePreviewDto> previewSchedule(@RequestBody DefenseScheduleRequestDto request) {
        return ResponseEntity.ok(scheduleService.generatePreview(request));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY')")
    public ResponseEntity<DefenseSchedulePreviewDto> generateSchedule(@RequestBody DefenseScheduleRequestDto request) {
        return ResponseEntity.ok(scheduleService.generateAndSave(request));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'METHODIST', 'SECRETARY', 'STUDENT', 'GEK_MEMBER')")
    public ResponseEntity<List<DefenseDayDto>> getScheduleByGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(scheduleService.getScheduleByGroup(groupId));
    }
}
