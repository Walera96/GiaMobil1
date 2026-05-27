package com.spbutu.gia.auth.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO для запроса входа в систему.
 */
public record LoginRequest(
        @NotBlank(message = "Имя пользователя обязательно")
        String username,

        @NotBlank(message = "Пароль обязателен")
        String password
) {
}
