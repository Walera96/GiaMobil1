package com.spbutu.gia.core.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Возможные оценки при голосовании ГЭК.
 * Хранится в БД как целое число (2–5).
 */
public enum ScoreValue {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5);

    private final int value;

    ScoreValue(int value) {
        this.value = value;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    /**
     * Получает enum по числовому значению.
     *
     * @param value числовая оценка
     * @return соответствующий ScoreValue
     * @throws IllegalArgumentException если значение вне диапазона 2–5
     */
    @JsonCreator
    public static ScoreValue fromValue(int value) {
        for (ScoreValue sv : values()) {
            if (sv.value == value) {
                return sv;
            }
        }
        throw new IllegalArgumentException("Недопустимая оценка: " + value);
    }
}
