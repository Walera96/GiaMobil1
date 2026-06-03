package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.NotificationSseService;
import com.spbutu.gia.auth.infrastructure.security.CustomUserDetails;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * SSE-контроллер для персональных real-time уведомлений.
 * Фронтенд подключается через EventSource с token в query param.
 */
@RestController
@RequestMapping("/sse")
public class NotificationSseController {

    private final NotificationSseService notificationSseService;

    public NotificationSseController(NotificationSseService notificationSseService) {
        this.notificationSseService = notificationSseService;
    }

    /**
     * Подписка на персональные уведомления.
     * GET /api/sse/notifications?token=...
     */
    @GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            SseEmitter emitter = new SseEmitter(0L);
            emitter.completeWithError(new SecurityException("Unauthorized"));
            return emitter;
        }
        Object principal = authentication.getPrincipal();
        UUID userId;
        if (principal instanceof CustomUserDetails custom) {
            userId = custom.getId();
        } else {
            userId = UUID.fromString(authentication.getName());
        }
        return notificationSseService.subscribe(userId);
    }
}
