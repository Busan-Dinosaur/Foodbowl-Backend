package org.dinosaur.foodbowl.domain.blame.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.global.entity.CreatedBaseEntity;

@Getter
@Entity
@Table(name = "blame")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blame extends CreatedBaseEntity {

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

    @Enumerated(value = EnumType.ORDINAL)
    @NotNull
    @Column(name = "target_type", updatable = false)
    private BlameTarget blameTarget;

    public enum BlameTarget {

        MEMBER,
        POST,
        COMMENT
    }
}
