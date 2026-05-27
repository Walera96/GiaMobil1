package com.spbutu.gia.core.domain.entity;

import com.spbutu.gia.core.domain.enums.ScoreValue;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Индивидуальный голос члена ГЭК за студента.
 * Один член ГЭК может проголосовать только один раз за пункт повестки.
 */
@Entity
@Table(
    name = "vote",
    uniqueConstraints = @UniqueConstraint(columnNames = {"agenda_item_id", "gek_member_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agenda_item_id", nullable = false)
    private AgendaItem agendaItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gek_member_id", nullable = false)
    private GekMember gekMember;

    @Column(nullable = false)
    private ScoreValue score;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Vote() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AgendaItem getAgendaItem() {
        return agendaItem;
    }

    public void setAgendaItem(AgendaItem agendaItem) {
        this.agendaItem = agendaItem;
    }

    public GekMember getGekMember() {
        return gekMember;
    }

    public void setGekMember(GekMember gekMember) {
        this.gekMember = gekMember;
    }

    public ScoreValue getScore() {
        return score;
    }

    public void setScore(ScoreValue score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getVotedAt() {
        return votedAt;
    }

    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
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
