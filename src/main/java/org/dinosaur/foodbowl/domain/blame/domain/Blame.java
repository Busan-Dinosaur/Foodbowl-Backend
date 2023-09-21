package org.dinosaur.foodbowl.domain.blame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.domain.vo.Description;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(
        name = "blame",
        uniqueConstraints = {
                @UniqueConstraint(name = "uniq_blame", columnNames = {"member_id", "target_id", "target_type"})
        },
        indexes = {@Index(name = "idx_blame", columnList = "target_id, target_type")}
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blame extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @NotNull
    @Column(name = "target_id", updatable = false)
    private Long targetId;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "target_type", updatable = false)
    private BlameTarget blameTarget;

    @Valid
    @Embedded
    private Description description;

    @Builder
    private Blame(Member member, Long targetId, BlameTarget blameTarget, String description) {
        this.member = member;
        this.targetId = targetId;
        this.blameTarget = blameTarget;
        this.description = new Description(description);
    }
}
