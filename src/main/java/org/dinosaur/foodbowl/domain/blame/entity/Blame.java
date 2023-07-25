package org.dinosaur.foodbowl.domain.blame.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.common.AuditingEntity;
import org.dinosaur.foodbowl.domain.member.entity.Member;

@Getter
@Entity
@Table(name = "blame")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blame extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @NotNull
    @Column(name = "target_id", updatable = false)
    private Long targetId;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Column(name = "target_type", updatable = false)
    private BlameTarget blameTarget;

    @Builder
    private Blame(Member member, Long targetId, BlameTarget blameTarget) {
        this.member = member;
        this.targetId = targetId;
        this.blameTarget = blameTarget;
    }

    public enum BlameTarget {

        MEMBER,
        REVIEW
    }
}