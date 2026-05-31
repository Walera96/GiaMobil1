package com.spbutu.gia.auth.infrastructure.config;

import com.spbutu.gia.auth.infrastructure.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Конфигурация Spring Security для модульной архитектуры с порталами.
 *
 * Структура URL:
 * - /auth/** — публичные (логин, refresh)
 * - /actuator/** — публичные (health)
 * - /portal/admin/** — SYSTEM_ADMIN, UNIVERSITY_ADMIN
 * - /portal/deanery/** — DEAN, DEAN_SECRETARY
 * - /portal/department/** — DEPARTMENT_HEAD, DEPARTMENT_SECRETARY, SUPERVISOR
 * - /portal/gek/** — GEK_SECRETARY, GEK_CHAIRMAN, GEK_MEMBER
 * - /portal/methodist/** — METHODIST
 * - /portal/student/** — STUDENT
 * - /api/** — общие API (требуют аутентификации)
 *
 * Переходная фаза: старые пути (/meetings, /protocols и т.д.) пока работают
 * с текущими ролями, но будут перенесены на /portal/gek/**.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // === ПУБЛИЧНЫЕ ===
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()

                // === ПОРТАЛ: АДМИНИСТРАЦИЯ ===
                .requestMatchers("/portal/admin/**")
                    .hasAnyRole("SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ПОРТАЛ: ДЕКАНАТ ===
                .requestMatchers("/portal/deanery/**")
                    .hasAnyRole("DEAN", "DEAN_SECRETARY", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")
                .requestMatchers("/portal/deanery/orders/approve", "/portal/deanery/orders/sign")
                    .hasAnyRole("DEAN", "SYSTEM_ADMIN")

                // === ПОРТАЛ: КАФЕДРА ===
                .requestMatchers("/portal/department/**")
                    .hasAnyRole("DEPARTMENT_HEAD", "DEPARTMENT_SECRETARY",
                        "SUPERVISOR", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ПОРТАЛ: ГЭК ===
                .requestMatchers("/portal/gek/meetings/**")
                    .hasAnyRole("GEK_SECRETARY", "GEK_CHAIRMAN", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")
                .requestMatchers("/portal/gek/voting/**")
                    .hasAnyRole("GEK_MEMBER", "GEK_SECRETARY", "GEK_CHAIRMAN")
                .requestMatchers("/portal/gek/protocols/**")
                    .hasAnyRole("GEK_SECRETARY", "GEK_CHAIRMAN", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")
                .requestMatchers("/portal/gek/vedomost/**")
                    .hasAnyRole("GEK_SECRETARY", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")
                .requestMatchers("/portal/gek/monitor/**")
                    .hasAnyRole("GEK_SECRETARY", "GEK_CHAIRMAN", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ПОРТАЛ: МЕТОДИСТ ===
                .requestMatchers("/portal/methodist/**")
                    .hasAnyRole("METHODIST", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ПОРТАЛ: СТУДЕНТ ===
                .requestMatchers("/portal/student/**")
                    .hasAnyRole("STUDENT", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ЗАДАНИЯ: ПРЕПОДАВАТЕЛЬ ===
                .requestMatchers("/api/teacher/**")
                    .hasAnyRole("SUPERVISOR", "DEPARTMENT_HEAD", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ЗАДАНИЯ: СТУДЕНТ ===
                .requestMatchers("/api/student/**")
                    .hasAnyRole("STUDENT", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === СТАРЫЕ ПУТИ (переходная совместимость) ===
                .requestMatchers("/protocols/**")
                    .hasAnyRole("GEK_SECRETARY", "GEK_CHAIRMAN", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")
                .requestMatchers("/testing/**").hasAnyRole("SYSTEM_ADMIN", "UNIVERSITY_ADMIN")
                .requestMatchers("/drafts/**").hasAnyRole("GEK_SECRETARY", "DEAN", "SYSTEM_ADMIN", "UNIVERSITY_ADMIN")

                // === ВСЁ ОСТАЛЬНОЕ ===
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "http://localhost:5173", "http://localhost:5174", "http://localhost:5175",
            "http://localhost:3000", "http://localhost:8081", "http://localhost:8082",
            "http://172.27.48.1:3000", "http://172.27.48.1:5173",
            // Порталы (будущие)
            "http://localhost:5178", "http://localhost:5179",
            "http://localhost:5180", "http://localhost:5181",
            "http://localhost:5182", "http://localhost:5183"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Portal"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
