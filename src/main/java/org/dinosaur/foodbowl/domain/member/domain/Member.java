package org.dinosaur.foodbowl.domain.member.domain;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_MEMBER", columnNames = {"social_id", "social_type"})
        },
        indexes = {@Index(name = "IDX_MEMBER", columnList = "social_id, social_type")}
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Column(name = "social_type", updatable = false, length = 45)
    private SocialType socialType;

    @NotNull
    @Column(name = "social_id", updatable = false, length = 512)
    private String socialId;

    @Column(name = "email", length = 255)
    private String email;

    @Valid
    @Embedded
    private Nickname nickname;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY)
    private MemberThumbnail memberThumbnail;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followings = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private List<Follow> followers = new ArrayList<>();

    @Builder
    private Member(
            SocialType socialType,
            String socialId,
            String email,
            String nickname,
            String introduction
    ) {
        this.socialType = socialType;
        this.socialId = socialId;
        this.email = email;
        this.nickname = new Nickname(nickname);
        this.introduction = introduction;
    }

    public boolean isFollowing(Member member) {
        return followings.stream()
                .map(Follow::getFollowing)
                .map(Member::getId)
                .anyMatch(followingMemberId -> Objects.equals(followingMemberId, member.getId()));
    }

    public String getNickname() {
        return this.nickname.getValue();
    }

    public String getProfileImageUrl() {
        return Optional.ofNullable(memberThumbnail)
                .map(MemberThumbnail::getThumbnail)
                .map(Thumbnail::getPath)
                .orElse(null);
    }

    public int getFollowerCount() {
        return followers.size();
    }
}
