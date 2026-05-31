package com.spbutu.gia.auth.web.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String fullName,
    String email,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
