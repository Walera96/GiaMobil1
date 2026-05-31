package com.spbutu.gia.auth.infrastructure.security;

import com.spbutu.gia.auth.domain.entity.AppUser;
import com.spbutu.gia.auth.domain.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация UserDetails для Spring Security.
 * Преобразует Set<UserRole> в коллекцию GrantedAuthority.
 *
 * Каждая роль мапится в authority вида "ROLE_<ROLE_NAME>"
 * (требование Spring Security).
 */
public class CustomUserDetails implements UserDetails {

    private final AppUser appUser;

    public CustomUserDetails(AppUser appUser) {
        this.appUser = appUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return appUser.getPassword();
    }

    @Override
    public String getUsername() {
        return appUser.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Получить ID пользователя.
     */
    public java.util.UUID getId() {
        return appUser.getId();
    }

    /**
     * Получить полное имя.
     */
    public String getFullName() {
        return appUser.getFullName();
    }

    /**
     * Получить email.
     */
    public String getEmail() {
        return appUser.getEmail();
    }

    /**
     * Получить роли пользователя.
     */
    public Set<UserRole> getRoles() {
        return appUser.getRoles();
    }

    /**
     * Проверить наличие роли.
     */
    public boolean hasRole(UserRole role) {
        return appUser.hasRole(role);
    }

    /**
     * Получить привязку к кафедре.
     */
    public java.util.UUID getDepartmentId() {
        return appUser.getDepartmentId();
    }

    /**
     * Получить привязку к группе.
     */
    public java.util.UUID getStudyGroupId() {
        return appUser.getStudyGroupId();
    }

    /**
     * Получить основной портал.
     */
    public String getPrimaryPortal() {
        return appUser.getPrimaryPortal();
    }
}
