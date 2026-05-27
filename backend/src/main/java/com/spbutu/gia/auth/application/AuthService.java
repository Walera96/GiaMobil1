package com.spbutu.gia.auth.application;

import com.spbutu.gia.auth.web.dto.LoginRequest;
import com.spbutu.gia.auth.web.dto.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Сервис аутентификации.
 * Обрабатывает вход по логину/паролю и обновление access-токена через refresh-токен.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    /**
     * Аутентифицирует пользователя и выдает пару токенов.
     *
     * @param request логин и пароль
     * @return access + refresh токены
     */
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("Успешная аутентификация: {}", userDetails.getUsername());

        return generateTokenResponse(userDetails);
    }

    /**
     * Обновляет access-токен по действительному refresh-токену.
     *
     * @param refreshToken refresh-токен
     * @return новая пара токенов
     */
    public TokenResponse refresh(String refreshToken) {
        if (!jwtService.isRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Недействительный refresh-токен");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new BadCredentialsException("Refresh-токен просрочен или недействителен");
        }

        log.info("Обновление токенов для пользователя: {}", username);
        return generateTokenResponse(userDetails);
    }

    private TokenResponse generateTokenResponse(UserDetails userDetails) {
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("");

        return new TokenResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtService.getAccessExpiration(),
                role,
                userDetails.getUsername()
        );
    }
}
