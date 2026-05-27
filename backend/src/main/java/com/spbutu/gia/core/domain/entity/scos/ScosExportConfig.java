package com.spbutu.gia.core.domain.entity.scos;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scos_export_config")
public class ScosExportConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "direction_code", nullable = false, unique = true)
    private String directionCode;

    @Column(name = "scos_direction_code")
    private String scosDirectionCode;

    @Column(name = "scos_direction_name")
    private String scosDirectionName;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getDirectionCode() { return directionCode; }
    public void setDirectionCode(String directionCode) { this.directionCode = directionCode; }

    public String getScosDirectionCode() { return scosDirectionCode; }
    public void setScosDirectionCode(String scosDirectionCode) { this.scosDirectionCode = scosDirectionCode; }

    public String getScosDirectionName() { return scosDirectionName; }
    public void setScosDirectionName(String scosDirectionName) { this.scosDirectionName = scosDirectionName; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
