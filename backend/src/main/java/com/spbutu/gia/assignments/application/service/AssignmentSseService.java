package com.spbutu.gia.assignments.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSE-сервис для real-time уведомлений студентов и преподавателей
 * о новых заданиях, сдачах и оценках.
 */
@Service
public class AssignmentSseService {

    /** Ключ — userId, значение — SseEmitter для этого пользователя */
    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Подписать пользователя на уведомления.
     */
    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(0L); // без таймаута
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Subscribed to assignments"));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * Отправить уведомление конкретному пользователю.
     */
    public void sendToUser(UUID userId, @org.springframework.lang.NonNull String eventName, @org.springframework.lang.NonNull Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException e) {
            emitters.remove(userId);
        }
    }

    /**
     * Отправить уведомление нескольким пользователям (например, всей группе).
     */
    public void sendToUsers(Iterable<UUID> userIds, @org.springframework.lang.NonNull String eventName, @org.springframework.lang.NonNull Object data) {
        for (UUID userId : userIds) {
            sendToUser(userId, eventName, data);
        }
    }
}
