package com.spbutu.gia.auth.web.dto;

import com.spbutu.gia.auth.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String fullName,
    String email,
    UserRole role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
