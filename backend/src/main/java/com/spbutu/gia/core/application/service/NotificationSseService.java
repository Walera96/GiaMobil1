package com.spbutu.gia.core.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для отправки персональных SSE-уведомлений пользователям.
 * Каждый пользователь подписывается на свой канал по userId.
 */
@SuppressWarnings("null")
@Service
public class NotificationSseService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Подписывает клиента на персональные уведомления.
     */
    public SseEmitter subscribe(UUID userId) {
        SseEmitter emitter = new SseEmitter(0L); // без таймаута
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Subscribed to notifications for user " + userId));
        } catch (IOException e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    /**
     * Отправляет уведомление конкретному пользователю.
     */
    public void sendToUser(UUID userId, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
            } catch (IOException e) {
                emitters.remove(userId);
            }
        }
    }

    /**
     * Отправляет уведомление всем подключённым пользователям.
     */
    public void broadcast(Object data) {
        emitters.values().forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
            } catch (IOException e) {
                // emitter will be removed by onError handler
            }
        });
    }
}
