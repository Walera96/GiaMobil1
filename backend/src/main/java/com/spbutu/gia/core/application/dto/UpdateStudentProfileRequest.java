package com.spbutu.gia.core.application.dto;

/**
 * DTO для обновления профиля студента.
 * Студент может редактировать только тему ВКР и имя руководителя.
 */
public record UpdateStudentProfileRequest(
        String thesisTopic,
        String supervisorName
) {
}
