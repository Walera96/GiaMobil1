package com.spbutu.gia.auth.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Запрос на обновление access-токена.
 */
public record RefreshRequest(
        @NotBlank String refreshToken
) {
}
