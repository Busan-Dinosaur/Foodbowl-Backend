package org.dinosaur.foodbowl.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.thumbnail.entity.Thumbnail;
import org.dinosaur.foodbowl.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "member")
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;

    @Column(name = "social_type", nullable = false, updatable = false, length = 45)
    private String socialType;

    @Column(name = "social_id", nullable = false, updatable = false, length = 512)
    private String socialId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 45)
    private String nickname;

    @Column(name = "introduce", length = 255)
    private String introduce;

    @Builder
    private Member(
            Thumbnail thumbnail, String socialType, String socialId, String email, String nickname, String introduce
    ) {
        this.thumbnail = thumbnail;
        this.socialType = socialType;
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.introduce = introduce;
    }
}
