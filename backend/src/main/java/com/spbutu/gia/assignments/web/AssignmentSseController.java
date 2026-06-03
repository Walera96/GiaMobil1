package com.spbutu.gia.assignments.web;

import com.spbutu.gia.assignments.application.service.AssignmentSseService;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * SSE-контроллер для уведомлений о заданиях.
 * Фронтенд подключается через EventSource.
 */
@RestController
@RequestMapping("/sse/assignments")
public class AssignmentSseController {

    private final AssignmentSseService assignmentSseService;

    public AssignmentSseController(AssignmentSseService assignmentSseService) {
        this.assignmentSseService = assignmentSseService;
    }

    /**
     * Подписка на уведомления текущего пользователя.
     * GET /api/sse/assignments
     */
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        return assignmentSseService.subscribe(userId);
    }
}
