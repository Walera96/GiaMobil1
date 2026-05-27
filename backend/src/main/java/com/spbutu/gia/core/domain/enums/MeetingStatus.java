package com.spbutu.gia.core.domain.enums;

/**
 * Статусы заседания ГЭК.
 */
public enum MeetingStatus {
    PLANNED,    // Запланировано
    ACTIVE,     // Активно (идет голосование)
    CLOSED,     // Завершено
    CANCELLED   // Отменено
}
