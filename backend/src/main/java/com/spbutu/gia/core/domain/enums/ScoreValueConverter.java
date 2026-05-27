package com.spbutu.gia.core.domain.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA-конвертер для хранения ScoreValue в БД как целого числа.
 * Применяется автоматически ко всем полям типа ScoreValue.
 */
@Converter(autoApply = true)
public class ScoreValueConverter implements AttributeConverter<ScoreValue, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ScoreValue attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public ScoreValue convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return ScoreValue.fromValue(dbData);
    }
}
