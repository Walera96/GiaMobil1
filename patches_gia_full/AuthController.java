package com.spbutu.gia.auth.web;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.enums.UserRole;
import com.spbutu.gia.auth.domain.repository.AppUserRepository;
import com.spbutu.gia.auth.infrastructure.security.CustomUserDetails;
import com.spbutu.gia.auth.infrastructure.security.JwtUtil;
import com.spbutu.gia.auth.web.dto.LoginRequest;
import com.spbutu.gia.auth.web.dto.RefreshRequest;
import com.spbutu.gia.auth.web.dto.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * Контроллер аутентификации.
 * Возвращает токены + роли + доступные порталы.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        AppUser user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Формируем ответ с ролями и порталами
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "fullName", user.getFullName(),
            "email", user.getEmail()
        ));
        response.put("roles", user.getRoles().stream().map(UserRole::name).toList());
        response.put("availablePortals", user.getAvailablePortals());
        response.put("primaryPortal", user.getPrimaryPortal() != null
            ? user.getPrimaryPortal()
            : determinePrimaryPortal(user.getRoles()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestBody RefreshRequest request) {
        String username = jwtUtil.extractUsername(request.refreshToken());
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        if (!jwtUtil.isTokenValid(request.refreshToken(), username)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        return ResponseEntity.ok(Map.of(
            "accessToken", newAccessToken,
            "refreshToken", newRefreshToken
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String username = auth.getName();
        AppUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "fullName", user.getFullName(),
            "email", user.getEmail(),
            "roles", user.getRoles().stream().map(UserRole::name).toList(),
            "availablePortals", user.getAvailablePortals(),
            "primaryPortal", user.getPrimaryPortal() != null
                ? user.getPrimaryPortal()
                : determinePrimaryPortal(user.getRoles()),
            "departmentId", user.getDepartmentId(),
            "studyGroupId", user.getStudyGroupId()
        ));
    }

    /**
     * Определить основной портал по приоритету ролей.
     */
    private String determinePrimaryPortal(Set<UserRole> roles) {
        // Приоритет: админ > деканат > ГЭК > кафедра > методист > студент
        if (roles.stream().anyMatch(UserRole::isAdminPortal)) return "admin";
        if (roles.stream().anyMatch(UserRole::isDeaneryPortal)) return "deanery";
        if (roles.stream().anyMatch(UserRole::isGekPortal)) return "gek";
        if (roles.stream().anyMatch(UserRole::isDepartmentPortal)) return "department";
        if (roles.contains(UserRole.METHODIST)) return "methodist";
        if (roles.contains(UserRole.STUDENT)) return "student";
        return "student";
    }
}
