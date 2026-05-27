package com.spbutu.gia.auth.web.dto;

import com.spbutu.gia.auth.domain.enums.UserRole;

public record CreateUserRequest(
    String username,
    String password,
    String fullName,
    String email,
    UserRole role
) {}
