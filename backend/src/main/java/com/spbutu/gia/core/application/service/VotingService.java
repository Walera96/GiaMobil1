package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.VoteDto;
import com.spbutu.gia.core.application.dto.VoteRequest;
import com.spbutu.gia.core.domain.entity.AgendaItem;
import com.spbutu.gia.core.domain.entity.Meeting;
import com.spbutu.gia.core.domain.entity.Vote;
import com.spbutu.gia.core.domain.enums.MeetingStatus;
import com.spbutu.gia.core.domain.repository.AgendaItemRepository;
import com.spbutu.gia.core.domain.repository.GekMemberRepository;
import com.spbutu.gia.core.domain.repository.MeetingRepository;
import com.spbutu.gia.core.domain.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис голосования членов ГЭК.
 * Регистрация голоса, подсчет средней оценки, аудит.
 */
@SuppressWarnings("null")
@Service
public class VotingService {

    private final VoteRepository voteRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final MeetingRepository meetingRepository;
    private final GekMemberRepository gekMemberRepository;
    private final AuditService auditService;
    private final SseNotificationService sseNotificationService;

    public VotingService(VoteRepository voteRepository,
                         AgendaItemRepository agendaItemRepository,
                         MeetingRepository meetingRepository,
                         GekMemberRepository gekMemberRepository,
                         AuditService auditService,
                         SseNotificationService sseNotificationService) {
        this.voteRepository = voteRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.meetingRepository = meetingRepository;
        this.gekMemberRepository = gekMemberRepository;
        this.auditService = auditService;
        this.sseNotificationService = sseNotificationService;
    }

    /**
     * Регистрирует голос члена ГЭК.
     * Проверяет, что заседание активно, и что голос уникален.
     *
     * @param gekMemberId ID члена ГЭК (из текущего пользователя)
     * @param request     данные голоса
     * @return сохраненный голос
     */
    @Transactional
    public VoteDto vote(UUID gekMemberId, String pinCode, VoteRequest request) {
        AgendaItem agendaItem = agendaItemRepository.findById(request.agendaItemId())
                .orElseThrow(() -> new IllegalArgumentException("Пункт повестки не найден: " + request.agendaItemId()));

        Meeting meeting = meetingRepository.findById(agendaItem.getMeeting().getId())
                .orElseThrow(() -> new IllegalArgumentException("Заседание не найдено"));

        if (meeting.getStatus() != MeetingStatus.ACTIVE) {
            throw new IllegalStateException("Голосование возможно только в активном заседании");
        }

        var gekMember = gekMemberRepository.findById(gekMemberId)
                .orElseThrow(() -> new IllegalArgumentException("Член ГЭК не найден"));

        if (pinCode != null && !pinCode.equals(gekMember.getPinCode())) {
            throw new IllegalArgumentException("Неверный PIN-код");
        }

        // Проверяем, что голос уже не подан
        List<Vote> existingVotes = voteRepository.findAllByAgendaItemId(request.agendaItemId());
        boolean alreadyVoted = existingVotes.stream()
                .anyMatch(v -> v.getGekMember().getId().equals(gekMemberId));
        if (alreadyVoted) {
            throw new IllegalStateException("Вы уже проголосовали за этого студента");
        }

        Vote vote = new Vote();
        vote.setAgendaItem(agendaItem);
        vote.setGekMember(gekMember);
        vote.setScore(request.score());
        vote.setComment(request.comment());
        vote.setVotedAt(LocalDateTime.now());

        Vote saved = voteRepository.save(vote);

        // Автоподсчёт средней оценки
        recalculateAverageScore(agendaItem);

        // SSE: уведомляем подписчиков заседания об обновлении
        List<Vote> allVotes = voteRepository.findAllByAgendaItemId(agendaItem.getId());
        sseNotificationService.sendVoteUpdate(meeting.getId(), Map.of(
                "agendaItemId", agendaItem.getId().toString(),
                "averageScore", agendaItem.getAverageScore(),
                "overallAverageScore", agendaItem.getOverallAverageScore(),
                "totalVotes", allVotes.size()
        ));

        // Аудит: фиксируем новый голос
        auditService.logChange(
                "vote",
                saved.getId(),
                com.spbutu.gia.core.domain.enums.AuditAction.INSERT,
                null,
                "score=" + request.score().getValue() + ", comment=" + request.comment(),
                null,
                null
        );

        return toDto(saved);
    }

    private void recalculateAverageScore(AgendaItem agendaItem) {
        List<Vote> votes = voteRepository.findAllByAgendaItemId(agendaItem.getId());
        double avg = votes.stream()
                .mapToInt(v -> v.getScore().getValue())
                .average()
                .orElse(0.0);
        double rounded = Math.round(avg * 100.0) / 100.0;
        agendaItem.setAverageScore(rounded);
        agendaItem.setOverallAverageScore(rounded);
        agendaItemRepository.save(agendaItem);
    }

    /**
     * Подсчитывает среднюю оценку по пункту повестки.
     *
     * @param agendaItemId ID пункта повестки
     * @return средняя оценка (округленная до целого) или null, если голосов нет
     */
    @Transactional(readOnly = true)
    public Integer calculateAverageScore(UUID agendaItemId) {
        List<Vote> votes = voteRepository.findAllByAgendaItemId(agendaItemId);
        if (votes.isEmpty()) {
            return null;
        }
        double avg = votes.stream()
                .mapToInt(v -> v.getScore().getValue())
                .average()
                .orElse(0.0);
        return (int) Math.round(avg);
    }

    /**
     * Получает общий средний балл студента по ID пункта повестки.
     */
    @Transactional(readOnly = true)
    public Double getStudentAverageScore(UUID agendaItemId) {
        AgendaItem agendaItem = agendaItemRepository.findById(agendaItemId)
                .orElseThrow(() -> new IllegalArgumentException("Пункт повестки не найден"));
        return agendaItem.getOverallAverageScore();
    }

    /**
     * Получает все голоса по пункту повестки с деталями.
     */
    @Transactional(readOnly = true)
    public List<VoteDto> getVotesByAgendaItem(UUID agendaItemId) {
        return voteRepository.findAllByAgendaItemId(agendaItemId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Завершает голосование по пункту повестки (для председателя).
     * Возвращает итоговый средний балл и все голоса.
     */
    @Transactional(readOnly = true)
    public Map<String, Object> finishVoting(UUID agendaItemId) {
        AgendaItem agendaItem = agendaItemRepository.findById(agendaItemId)
                .orElseThrow(() -> new IllegalArgumentException("Пункт повестки не найден"));
        List<VoteDto> votes = getVotesByAgendaItem(agendaItemId);
        return Map.of(
                "agendaItemId", agendaItemId,
                "overallAverageScore", agendaItem.getOverallAverageScore() != null ? agendaItem.getOverallAverageScore() : 0.0,
                "totalVotes", votes.size(),
                "votes", votes
        );
    }

    private VoteDto toDto(Vote vote) {
        var gekMember = vote.getGekMember();
        String gekMemberName = null;
        if (gekMember != null && gekMember.getUser() != null) {
            gekMemberName = gekMember.getUser().getFullName();
        }
        return new VoteDto(
                vote.getId(),
                vote.getAgendaItem() != null ? vote.getAgendaItem().getId() : null,
                gekMember != null ? gekMember.getId() : null,
                gekMemberName,
                vote.getScore(),
                vote.getComment(),
                vote.getVotedAt()
        );
    }
}
