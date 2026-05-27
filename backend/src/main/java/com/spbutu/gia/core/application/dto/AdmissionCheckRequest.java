package com.spbutu.gia.core.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Запрос на проверку допуска студента.
 */
public record AdmissionCheckRequest(
        @NotNull(message = "ID студента обязательно")
        UUID studentId,

        @Min(value = 0, message = "Баллы БРС не могут быть меньше 0")
        @Max(value = 100, message = "Баллы БРС не могут быть больше 100")
        Integer brsScore,

        @NotNull(message = "Указание наличия задолженностей обязательно")
        Boolean hasDebt
) {
}
