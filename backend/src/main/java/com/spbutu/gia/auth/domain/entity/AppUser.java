package com.spbutu.gia.auth.domain.entity;

import com.spbutu.gia.auth.domain.enums.UserRole;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Пользователь системы.
 * Поддерживает множественные роли для доступа к разным порталам.
 *
 * Пример: один человек может быть METHODIST + GEK_MEMBER.
 * Тогда у него доступ к portal-methodist и portal-gek.
 */
@Entity
@Table(name = "app_user")
@EntityListeners(AuditingEntityListener.class)
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(length = 100)
    private String email;

    @Column(name = "full_name", length = 200)
    private String fullName;

    /**
     * Множественные роли пользователя.
     * Хранятся в отдельной таблице user_roles (UserRole многие-ко-многим).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Set<UserRole> roles = new HashSet<>();

    /**
     * Привязка к кафедре (для ролей DEPARTMENT_HEAD, DEPARTMENT_SECRETARY, SUPERVISOR).
     */
    @Column(name = "department_id")
    private UUID departmentId;

    /**
     * Привязка к учебной группе (для роли STUDENT).
     */
    @Column(name = "study_group_id")
    private UUID studyGroupId;

    /**
     * Основной портал для редиректа после входа.
     * Определяется автоматически по приоритету ролей.
     */
    @Column(name = "primary_portal", length = 30)
    private String primaryPortal;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ========== Конструкторы ==========

    public AppUser() {
    }

    // ========== Геттеры / Сеттеры ==========

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<UserRole> roles) {
        this.roles = roles;
    }

    /**
     * Добавить роль к пользователю.
     */
    public void addRole(UserRole role) {
        this.roles.add(role);
    }

    /**
     * Удалить роль у пользователя.
     */
    public void removeRole(UserRole role) {
        this.roles.remove(role);
    }

    /**
     * Проверить, имеет ли пользователь указанную роль.
     */
    public boolean hasRole(UserRole role) {
        return this.roles.contains(role);
    }

    /**
     * Проверить, имеет ли пользователь хотя бы одну из указанных ролей.
     */
    public boolean hasAnyRole(UserRole... checkRoles) {
        for (UserRole r : checkRoles) {
            if (this.roles.contains(r)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Получить список доступных порталов.
     */
    public Set<String> getAvailablePortals() {
        Set<String> portals = new HashSet<>();
        for (UserRole role : roles) {
            portals.add(role.getPortal());
        }
        return portals;
    }

    public UUID getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(UUID departmentId) {
        this.departmentId = departmentId;
    }

    public UUID getStudyGroupId() {
        return studyGroupId;
    }

    public void setStudyGroupId(UUID studyGroupId) {
        this.studyGroupId = studyGroupId;
    }

    public String getPrimaryPortal() {
        return primaryPortal;
    }

    public void setPrimaryPortal(String primaryPortal) {
        this.primaryPortal = primaryPortal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
