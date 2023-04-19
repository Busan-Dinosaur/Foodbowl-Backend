package org.dinosaur.foodbowl.domain.follow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.global.entity.CreatedBaseEntity;

@Getter
@Entity
@Table(name = "follow")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends CreatedBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "following_id")
    private Member following;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "follower_id")
    private Member follower;

    @Builder
    private Follow(Member following, Member follower) {
        this.following = following;
        this.follower = follower;
    }
}

