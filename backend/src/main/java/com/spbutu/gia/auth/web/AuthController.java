package com.spbutu.gia.auth.web;

import com.spbutu.gia.auth.application.AuthService;
import com.spbutu.gia.auth.web.dto.LoginRequest;
import com.spbutu.gia.auth.web.dto.RefreshRequest;
import com.spbutu.gia.auth.web.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер аутентификации.
 * Публичные эндпоинты (не требуют JWT).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Вход в систему.
     * POST /api/auth/login
     *
     * @param request логин и пароль
     * @return пара access/refresh токенов
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /**
     * Обновление access-токена по refresh-токену.
     * POST /api/auth/refresh
     *
     * @param request refresh-токен (в теле запроса)
     * @return новая пара токенов
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody @Valid RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }
}
