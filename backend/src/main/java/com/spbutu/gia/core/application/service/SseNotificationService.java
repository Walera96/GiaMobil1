package com.spbutu.gia.core.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для отправки SSE-уведомлений клиентам.
 * Используется VotingService для real-time обновления табло голосования.
 */
@SuppressWarnings("null")
@Service
public class SseNotificationService {

    private final Map<UUID, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * Подписывает клиента на события заседания.
     */
    public SseEmitter subscribe(UUID meetingId) {
        SseEmitter emitter = new SseEmitter(0L); // без таймаута
        emitters.put(meetingId, emitter);

        emitter.onCompletion(() -> emitters.remove(meetingId));
        emitter.onTimeout(() -> emitters.remove(meetingId));
        emitter.onError((e) -> emitters.remove(meetingId));

        try {
            emitter.send(SseEmitter.event().name("connected").data("Subscribed to meeting " + meetingId));
        } catch (IOException e) {
            emitters.remove(meetingId);
        }

        return emitter;
    }

    /**
     * Отправляет событие обновления голосования всем подписчикам заседания.
     */
    public void sendVoteUpdate(UUID meetingId, Object data) {
        SseEmitter emitter = emitters.get(meetingId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("vote-update").data(data));
            } catch (IOException e) {
                emitters.remove(meetingId);
            }
        }
    }
}
