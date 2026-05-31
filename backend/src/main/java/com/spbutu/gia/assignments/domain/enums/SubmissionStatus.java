package com.spbutu.gia.assignments.domain.enums;

/**
 * Статус сдачи задания студентом.
 */
public enum SubmissionStatus {
    DRAFT,      // Черновик
    SUBMITTED,  // Отправлено на проверку
    REVIEWING,  // На проверке преподавателем
    REVIEWED,   // Проверено, оценка выставлена
    RETURNED    // Возвращено на доработку
}
