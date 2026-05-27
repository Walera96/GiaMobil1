package com.spbutu.gia.core.application.service.schedule;

import com.spbutu.gia.core.application.dto.schedule.*;
import com.spbutu.gia.core.domain.entity.*;
import com.spbutu.gia.core.domain.enums.MeetingStatus;
import com.spbutu.gia.core.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class DefenseScheduleService {

    private final StudentRepository studentRepository;
    private final AdmissionRepository admissionRepository;
    private final MeetingRepository meetingRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final StudyGroupRepository groupRepository;
    private final GekRepository gekRepository;

    public DefenseScheduleService(StudentRepository studentRepository,
                                  AdmissionRepository admissionRepository,
                                  MeetingRepository meetingRepository,
                                  AgendaItemRepository agendaItemRepository,
                                  StudyGroupRepository groupRepository,
                                  GekRepository gekRepository) {
        this.studentRepository = studentRepository;
        this.admissionRepository = admissionRepository;
        this.meetingRepository = meetingRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.groupRepository = groupRepository;
        this.gekRepository = gekRepository;
    }

    @Transactional(readOnly = true)
    public DefenseSchedulePreviewDto generatePreview(DefenseScheduleRequestDto request) {
        return buildSchedule(request, false);
    }

    @Transactional
    public DefenseSchedulePreviewDto generateAndSave(DefenseScheduleRequestDto request) {
        return buildSchedule(request, true);
    }

    private DefenseSchedulePreviewDto buildSchedule(DefenseScheduleRequestDto request, boolean save) {
        List<String> warnings = new ArrayList<>();

        // Получаем admitted студентов
        List<Student> students = getAdmittedStudents(request, warnings);
        if (students.isEmpty()) {
            throw new IllegalArgumentException("Нет допущенных студентов для формирования расписания");
        }

        // Сортируем
        sortStudents(students, request.getSortBy());

        // Параметры планирования
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : startDate;
        LocalTime dayStart = request.getDayStartTime() != null ? request.getDayStartTime() : LocalTime.of(9, 0);
        LocalTime dayEnd = request.getDayEndTime() != null ? request.getDayEndTime() : LocalTime.of(17, 0);
        int slotMinutes = request.getSlotDurationMinutes() != null ? request.getSlotDurationMinutes() : 30;
        int breakMinutes = request.getBreakDurationMinutes() != null ? request.getBreakDurationMinutes() : 10;
        List<String> locations = request.getLocations() != null && !request.getLocations().isEmpty()
                ? request.getLocations()
                : Collections.singletonList(request.getLocation() != null ? request.getLocation() : "Ауд. 301");

        Gek gek = null;
        if (request.getGekId() != null) {
            gek = gekRepository.findById(request.getGekId()).orElse(null);
        }
        if (gek == null) {
            List<Gek> allGeks = gekRepository.findAll();
            if (!allGeks.isEmpty()) {
                gek = allGeks.get(0);
            }
        }

        // Распределяем по дням
        List<DefenseDayDto> days = new ArrayList<>();
        int studentIndex = 0;
        LocalDate currentDate = startDate;

        while (studentIndex < students.size() && !currentDate.isAfter(endDate)) {
            DefenseDayDto day = new DefenseDayDto();
            day.setDate(currentDate);
            day.setLocation(locations.get(0));
            day.setGekName(gek != null ? gek.getName() : null);

            List<DefenseSlotDto> slots = new ArrayList<>();
            LocalDateTime currentSlotStart = LocalDateTime.of(currentDate, dayStart);
            LocalDateTime dayEndDt = LocalDateTime.of(currentDate, dayEnd);
            int orderNumber = 1;

            while (studentIndex < students.size() && currentSlotStart.plusMinutes(slotMinutes).isBefore(dayEndDt.plusSeconds(1))) {
                Student student = students.get(studentIndex);
                DefenseSlotDto slot = new DefenseSlotDto();
                slot.setStudentId(student.getId());
                slot.setStudentName(student.getLastName() + " " + student.getFirstName() + " " + (student.getMiddleName() != null ? student.getMiddleName() : ""));
                slot.setRecordBookNumber(student.getRecordBookNumber());
                slot.setThesisTopic(student.getThesisTopic());
                slot.setSupervisorName(student.getSupervisorName());
                slot.setOrderNumber(orderNumber++);
                slot.setStartTime(currentSlotStart);
                slot.setEndTime(currentSlotStart.plusMinutes(slotMinutes));
                slot.setLocation(locations.get(0));
                slot.setPresentationDuration(slotMinutes);
                slots.add(slot);

                studentIndex++;
                currentSlotStart = currentSlotStart.plusMinutes(slotMinutes + breakMinutes);
            }

            day.setSlots(slots);
            day.setTotalSlots(slots.size());
            days.add(day);
            currentDate = currentDate.plusDays(1);
        }

        if (studentIndex < students.size()) {
            warnings.add("Не все студенты вместились в заданный период. Осталось " + (students.size() - studentIndex) + " студентов.");
        }

        // Сохраняем, если нужно
        if (save) {
            for (DefenseDayDto day : days) {
                Meeting meeting = new Meeting();
                meeting.setMeetingDate(LocalDateTime.of(day.getDate(), dayStart));
                meeting.setStartTime(dayStart);
                meeting.setEndTime(dayEnd);
                meeting.setLocation(day.getLocation());
                meeting.setStatus(MeetingStatus.PLANNED);
                meeting.setQuorumRequired(3);
                if (gek != null) {
                    meeting.setGek(gek);
                }
                meeting = meetingRepository.save(meeting);
                day.setMeetingId(meeting.getId());

                for (DefenseSlotDto slot : day.getSlots()) {
                    AgendaItem item = new AgendaItem();
                    item.setMeeting(meeting);
                    Student student = students.stream()
                            .filter(s -> s.getId().equals(slot.getStudentId()))
                            .findFirst().orElse(null);
                    item.setStudent(student);
                    item.setOrderNumber(slot.getOrderNumber());
                    item.setPresentationDuration(slotMinutes);
                    item.setScheduledTime(slot.getStartTime());
                    agendaItemRepository.save(item);
                }
            }
        }

        DefenseSchedulePreviewDto result = new DefenseSchedulePreviewDto();
        result.setTotalStudents(students.size());
        result.setTotalDays(days.size());
        result.setDays(days);
        result.setWarnings(warnings);

        if (request.getGroupId() != null) {
            groupRepository.findById(request.getGroupId()).ifPresent(g -> {
                result.setGroupName(g.getName());
                if (g.getDirection() != null) {
                    result.setDirectionName(g.getDirection().getName());
                }
            });
        }

        return result;
    }

    private List<Student> getAdmittedStudents(DefenseScheduleRequestDto request, List<String> warnings) {
        List<Student> allStudents;
        if (request.getGroupId() != null) {
            allStudents = studentRepository.findAll().stream()
                    .filter(s -> s.getGroup() != null && request.getGroupId().equals(s.getGroup().getId()))
                    .collect(Collectors.toList());
        } else if (request.getDirectionId() != null) {
            allStudents = studentRepository.findAll().stream()
                    .filter(s -> s.getGroup() != null && s.getGroup().getDirection() != null
                            && request.getDirectionId().equals(s.getGroup().getDirection().getId()))
                    .collect(Collectors.toList());
        } else {
            allStudents = studentRepository.findAll();
        }

        // Фильтруем только admitted
        List<Admission> admissions = admissionRepository.findByIsAdmittedTrue();
        Set<UUID> admittedIds = admissions.stream()
                .map(a -> a.getStudent() != null ? a.getStudent().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Student> result = new ArrayList<>();
        for (Student s : allStudents) {
            if (admittedIds.contains(s.getId())) {
                result.add(s);
            }
        }

        if (result.size() < allStudents.size()) {
            warnings.add("Отфильтровано " + (allStudents.size() - result.size()) + " не допущенных студентов");
        }

        return result;
    }

    private void sortStudents(List<Student> students, String sortBy) {
        if ("RANDOM".equalsIgnoreCase(sortBy)) {
            Collections.shuffle(students);
        } else if ("AVG_SCORE".equalsIgnoreCase(sortBy)) {
            // Упрощённо — по алфавиту, т.к. средний балл не хранится напрямую в Student
            students.sort(Comparator.comparing(Student::getLastName, Comparator.nullsLast(String::compareTo)));
        } else {
            // ALPHABETIC по умолчанию
            students.sort(Comparator.comparing(Student::getLastName, Comparator.nullsLast(String::compareTo)));
        }
    }

    @Transactional(readOnly = true)
    public List<DefenseDayDto> getScheduleByGroup(UUID groupId) {
        List<Student> students = studentRepository.findAll().stream()
                .filter(s -> s.getGroup() != null && groupId.equals(s.getGroup().getId()))
                .collect(Collectors.toList());

        Set<UUID> studentIds = students.stream().map(Student::getId).collect(Collectors.toSet());
        List<AgendaItem> items = agendaItemRepository.findAll().stream()
                .filter(ai -> ai.getStudent() != null && studentIds.contains(ai.getStudent().getId()))
                .collect(Collectors.toList());

        Map<UUID, List<AgendaItem>> byMeeting = items.stream()
                .collect(Collectors.groupingBy(ai -> ai.getMeeting().getId()));

        List<DefenseDayDto> days = new ArrayList<>();
        for (Map.Entry<UUID, List<AgendaItem>> entry : byMeeting.entrySet()) {
            Meeting meeting = entry.getValue().get(0).getMeeting();
            DefenseDayDto day = new DefenseDayDto();
            day.setMeetingId(meeting.getId());
            day.setDate(meeting.getMeetingDate() != null ? meeting.getMeetingDate().toLocalDate() : null);
            day.setLocation(meeting.getLocation());
            day.setGekName(meeting.getGek() != null ? meeting.getGek().getName() : null);

            List<DefenseSlotDto> slots = new ArrayList<>();
            for (AgendaItem ai : entry.getValue().stream().sorted(Comparator.comparing(AgendaItem::getOrderNumber)).toList()) {
                Student s = ai.getStudent();
                DefenseSlotDto slot = new DefenseSlotDto();
                slot.setStudentId(s.getId());
                slot.setStudentName(s.getLastName() + " " + s.getFirstName() + " " + (s.getMiddleName() != null ? s.getMiddleName() : ""));
                slot.setRecordBookNumber(s.getRecordBookNumber());
                slot.setThesisTopic(s.getThesisTopic());
                slot.setSupervisorName(s.getSupervisorName());
                slot.setOrderNumber(ai.getOrderNumber());
                slot.setStartTime(ai.getScheduledTime());
                slot.setPresentationDuration(ai.getPresentationDuration());
                slots.add(slot);
            }
            day.setSlots(slots);
            day.setTotalSlots(slots.size());
            days.add(day);
        }

        days.sort(Comparator.comparing(DefenseDayDto::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
        return days;
    }
}
