package com.spbutu.gia.auth.web.dto;

/**
 * DTO с токенами, возвращаемое после успешной аутентификации.
 */
public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String role,
        String username
) {
}
