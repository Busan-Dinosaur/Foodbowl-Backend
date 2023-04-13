package org.dinosaur.foodbowl.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.global.entity.BaseEntity;

@Getter
@Entity
@Table(name = "member")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", nullable = false, updatable = false, length = 45)
    private SocialType socialType;

    @Column(name = "social_id", nullable = false, updatable = false, length = 512)
    private String socialId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 45)
    private String nickname;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @Column(name = "region_1depth_name", length = 45)
    private String region1depthName;

    @Column(name = "region_2depth_name", length = 45)
    private String region2depthName;

    @Builder
    private Member(
            Thumbnail thumbnail,
            SocialType socialType,
            String socialId,
            String email,
            String nickname,
            String introduction,
            String region1depthName,
            String region2depthName
    ) {
        this.thumbnail = thumbnail;
        this.socialType = socialType;
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.introduction = introduction;
        this.region1depthName = region1depthName;
        this.region2depthName = region2depthName;
    }

    public enum SocialType {

        APPLE;
    }
}
