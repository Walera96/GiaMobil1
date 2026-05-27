package com.spbutu.gia.core.web;

import com.spbutu.gia.core.application.service.SseNotificationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * SSE-контроллер для real-time обновлений оценок на заседании.
 * Фронтенд подключается через EventSource.
 */
@RestController
@RequestMapping("/sse")
public class SseController {

    private final SseNotificationService sseNotificationService;

    public SseController(SseNotificationService sseNotificationService) {
        this.sseNotificationService = sseNotificationService;
    }

    /**
     * Подписка на события заседания.
     * GET /api/sse/meetings/{id}
     */
    @GetMapping(value = "/meetings/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable UUID id) {
        return sseNotificationService.subscribe(id);
    }
}
