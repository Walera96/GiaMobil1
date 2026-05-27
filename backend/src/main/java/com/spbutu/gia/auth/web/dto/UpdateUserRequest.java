package com.spbutu.gia.auth.web.dto;

import com.spbutu.gia.auth.domain.enums.UserRole;

public record UpdateUserRequest(
    String username,
    String fullName,
    String email,
    UserRole role
) {}
