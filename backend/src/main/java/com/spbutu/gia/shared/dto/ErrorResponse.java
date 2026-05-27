package com.spbutu.gia.shared.dto;

import java.time.Instant;

/**
 * Единый формат ошибок API.
 */
public record ErrorResponse(
        String code,
        String message,
        String timestamp
) {
    public ErrorResponse(String code, String message) {
        this(code, message, Instant.now().toString());
    }
}
