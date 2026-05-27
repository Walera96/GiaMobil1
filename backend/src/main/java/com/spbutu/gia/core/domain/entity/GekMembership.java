package com.spbutu.gia.core.domain.entity;

import com.spbutu.gia.core.domain.enums.GekPosition;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Состав ГЭК (связь члена с конкретной комиссией).
 * Один преподаватель может входить в разные ГЭК в разные годы.
 */
@Entity
@Table(
    name = "gek_membership",
    uniqueConstraints = @UniqueConstraint(columnNames = {"gek_id", "gek_member_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class GekMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gek_id", nullable = false)
    private Gek gek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gek_member_id", nullable = false)
    private GekMember gekMember;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_in_gek", nullable = false, length = 20)
    private GekPosition positionInGek;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public GekMembership() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Gek getGek() {
        return gek;
    }

    public void setGek(Gek gek) {
        this.gek = gek;
    }

    public GekMember getGekMember() {
        return gekMember;
    }

    public void setGekMember(GekMember gekMember) {
        this.gekMember = gekMember;
    }

    public GekPosition getPositionInGek() {
        return positionInGek;
    }

    public void setPositionInGek(GekPosition positionInGek) {
        this.positionInGek = positionInGek;
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
