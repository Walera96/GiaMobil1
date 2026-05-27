package com.spbutu.gia.core.application.service;

import com.spbutu.gia.core.application.dto.CreateMeetingRequest;
import com.spbutu.gia.core.application.dto.MeetingDto;
import com.spbutu.gia.core.domain.entity.Meeting;
import com.spbutu.gia.core.domain.enums.MeetingStatus;
import com.spbutu.gia.core.domain.repository.AgendaItemRepository;
import com.spbutu.gia.core.domain.repository.GekMembershipRepository;
import com.spbutu.gia.core.domain.repository.GekRepository;
import com.spbutu.gia.core.domain.repository.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис управления заседаниями ГЭК.
 * Создание, активация, проверка кворума, закрытие.
 */
@SuppressWarnings("null")
@Service
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final GekMembershipRepository gekMembershipRepository;
    private final GekRepository gekRepository;
    private final AgendaItemRepository agendaItemRepository;

    public MeetingService(MeetingRepository meetingRepository,
                          GekMembershipRepository gekMembershipRepository,
                          GekRepository gekRepository,
                          AgendaItemRepository agendaItemRepository) {
        this.meetingRepository = meetingRepository;
        this.gekMembershipRepository = gekMembershipRepository;
        this.gekRepository = gekRepository;
        this.agendaItemRepository = agendaItemRepository;
    }

    /**
     * Создает новое заседание ГЭК.
     */
    @Transactional
    public MeetingDto createMeeting(CreateMeetingRequest request, UUID createdById) {
        Meeting meeting = new Meeting();
        var gek = gekRepository.findById(request.gekId())
                .orElseThrow(() -> new IllegalArgumentException("ГЭК не найден: " + request.gekId()));
        meeting.setGek(gek);
        meeting.setMeetingDate(request.meetingDate());
        meeting.setLocation(request.location());
        meeting.setStatus(MeetingStatus.PLANNED);
        meeting.setQuorumRequired(request.quorumRequired() != null ? request.quorumRequired() : 3);

        Meeting saved = meetingRepository.save(meeting);
        return toDto(saved);
    }

    /**
     * Активирует заседание.
     * Проверяет кворум (минимум 3 члена ГЭК в составе).
     *
     * @throws IllegalStateException если кворум не достигнут
     */
    @Transactional
    public MeetingDto activateMeeting(UUID meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Заседание не найдено: " + meetingId));

        if (meeting.getStatus() != MeetingStatus.PLANNED) {
            throw new IllegalStateException("Заседание можно активировать только из статуса PLANNED");
        }

        int membersCount = gekMembershipRepository.findAllByGekId(meeting.getGek().getId()).size();
        if (membersCount < meeting.getQuorumRequired()) {
            throw new IllegalStateException(
                    "Кворум не достигнут. Требуется " + meeting.getQuorumRequired() +
                    " членов ГЭК, в составе " + membersCount);
        }

        meeting.setStatus(MeetingStatus.ACTIVE);
        Meeting saved = meetingRepository.save(meeting);
        return toDto(saved);
    }

    /**
     * Закрывает заседание.
     */
    @Transactional
    public MeetingDto closeMeeting(UUID meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Заседание не найдено: " + meetingId));

        if (meeting.getStatus() != MeetingStatus.ACTIVE) {
            throw new IllegalStateException("Заседание можно закрыть только из статуса ACTIVE");
        }

        meeting.setStatus(MeetingStatus.CLOSED);
        Meeting saved = meetingRepository.save(meeting);
        return toDto(saved);
    }

    /**
     * Получает заседание по ID.
     */
    @Transactional(readOnly = true)
    public MeetingDto getMeeting(UUID meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Заседание не найдено: " + meetingId));
        return toDto(meeting);
    }

    /**
     * Получает список всех заседаний.
     */
    @Transactional(readOnly = true)
    public List<MeetingDto> getAllMeetings() {
        return meetingRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Находит текущее активное заседание (для мобильного приложения).
     */
    @Transactional(readOnly = true)
    public com.spbutu.gia.core.application.dto.ActiveMeetingDto findActiveMeeting() {
        Meeting meeting = meetingRepository.findAll().stream()
                .filter(m -> m.getStatus() == MeetingStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Нет активных заседаний"));
        var items = agendaItemRepository.findAllByMeetingId(meeting.getId());
        var agendaDtos = items.stream()
                .map(item -> {
                    var student = item.getStudent();
                    return new com.spbutu.gia.core.application.dto.AgendaItemDto(
                            item.getId(),
                            meeting.getId(),
                            student != null ? student.getId() : null,
                            student != null ? student.getLastName() + " " + student.getFirstName() : null,
                            student != null ? student.getRecordBookNumber() : null,
                            student != null ? student.getThesisTopic() : null,
                            student != null ? student.getSupervisorName() : null,
                            item.getPresentationDuration(),
                            item.getAverageScore(),
                            item.getOverallAverageScore(),
                            null,
                            null,
                            student != null ? student.getThesisFilePath() : null,
                            student != null ? student.getThesisFileName() : null
                    );
                })
                .collect(Collectors.toList());
        return new com.spbutu.gia.core.application.dto.ActiveMeetingDto(
                meeting.getId(),
                meeting.getLocation(),
                meeting.getMeetingDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getLocation(),
                agendaDtos
        );
    }

    /**
     * Проверяет достижение кворума для заседания.
     */
    @Transactional(readOnly = true)
    public boolean checkQuorum(UUID meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Заседание не найдено: " + meetingId));
        int membersCount = gekMembershipRepository.findAllByGekId(meeting.getGek().getId()).size();
        return membersCount >= meeting.getQuorumRequired();
    }

    private MeetingDto toDto(Meeting meeting) {
        return new MeetingDto(
                meeting.getId(),
                meeting.getGek() != null ? meeting.getGek().getId() : null,
                meeting.getMeetingDate(),
                meeting.getStartTime(),
                meeting.getEndTime(),
                meeting.getLocation(),
                meeting.getStatus(),
                meeting.getQuorumRequired(),
                meeting.getCreatedBy() != null ? meeting.getCreatedBy().getId() : null,
                meeting.getCreatedAt()
        );
    }
}
