package com.spbutu.gia.auth.domain.enums;

/**
 * Роли пользователей в системе.
 * Хранится в БД как строка (VARCHAR) для читаемости.
 *
 * Архитектура порталов:
 * - SYSTEM_ADMIN, UNIVERSITY_ADMIN → portal-admin
 * - DEAN, DEAN_SECRETARY → portal-deanery
 * - DEPARTMENT_HEAD, DEPARTMENT_SECRETARY, SUPERVISOR → portal-department
 * - GEK_SECRETARY, GEK_CHAIRMAN, GEK_MEMBER → portal-gek
 * - METHODIST → portal-methodist
 * - STUDENT → portal-student
 */
public enum UserRole {
    // === СИСТЕМНЫЙ УРОВЕНЬ ===
    SYSTEM_ADMIN,        // Пользователи, роли, аудит, резервное копирование
    UNIVERSITY_ADMIN,    // Справочники, настройки ГИА, отчёты по вузу

    // === ДЕКАНАТ ===
    DEAN,                // Утверждение приказов, контингент
    DEAN_SECRETARY,      // Оформление приказов, движение студентов

    // === КАФЕДРА ===
    DEPARTMENT_HEAD,     // Дисциплины, преподаватели, нагрузка
    DEPARTMENT_SECRETARY,// Документооборот кафедры
    SUPERVISOR,          // Руководители ВКР (преподаватели)

    // === КОМИССИЯ ГИА (ГЭК) ===
    GEK_SECRETARY,       // Создание заседаний, протоколы, ведомости
    GEK_CHAIRMAN,        // Утверждение, контроль кворума
    GEK_MEMBER,          // Голосование по PIN-коду

    // === МЕТОДИСТЫ ===
    METHODIST,           // Импорт студентов, допуски, расписание

    // === СТУДЕНТЫ ===
    STUDENT;             // Личный кабинет ГИА, статус, оценки

    /**
     * Возвращает код портала для роли.
     */
    public String getPortal() {
        return switch (this) {
            case SYSTEM_ADMIN, UNIVERSITY_ADMIN -> "admin";
            case DEAN, DEAN_SECRETARY -> "deanery";
            case DEPARTMENT_HEAD, DEPARTMENT_SECRETARY, SUPERVISOR -> "department";
            case GEK_SECRETARY, GEK_CHAIRMAN, GEK_MEMBER -> "gek";
            case METHODIST -> "methodist";
            case STUDENT -> "student";
        };
    }

    /**
     * Роли, которые могут входить в несколько порталов (администраторы).
     */
    public boolean isMultiPortal() {
        return this == SYSTEM_ADMIN || this == UNIVERSITY_ADMIN;
    }

    /**
     * Роли, имеющие доступ к административному порталу.
     */
    public boolean isAdminPortal() {
        return this == SYSTEM_ADMIN || this == UNIVERSITY_ADMIN;
    }

    /**
     * Роли, имеющие доступ к порталу ГЭК.
     */
    public boolean isGekPortal() {
        return this == GEK_SECRETARY || this == GEK_CHAIRMAN || this == GEK_MEMBER;
    }

    /**
     * Роли деканата.
     */
    public boolean isDeaneryPortal() {
        return this == DEAN || this == DEAN_SECRETARY;
    }

    /**
     * Роли кафедры.
     */
    public boolean isDepartmentPortal() {
        return this == DEPARTMENT_HEAD || this == DEPARTMENT_SECRETARY || this == SUPERVISOR;
    }
}
