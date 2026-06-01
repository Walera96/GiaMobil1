package com.spbutu.gia.assignments.domain.enums;

/**
 * Статусы сдачи задания студентом.
 */
public enum SubmissionStatus {
    /** Черновик — студент готовит решение, ещё не сдал */
    DRAFT,
    /** Отправлено на проверку */
    SUBMITTED,
    /** На проверке у преподавателя */
    REVIEWING,
    /** Проверено, оценка выставлена */
    REVIEWED,
    /** Возвращено на доработку */
    RETURNED
}
