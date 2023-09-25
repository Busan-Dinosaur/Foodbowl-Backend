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
import jakarta.persistence.OneToMany;
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
import org.dinosaur.foodbowl.domain.member.domain.vo.Introduction;
import org.dinosaur.foodbowl.domain.member.domain.vo.Nickname;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;
import org.hibernate.annotations.Formula;

@Getter
@Entity
@Table(
        name = "member",
        uniqueConstraints = {@UniqueConstraint(name = "uniq_social_info", columnNames = {"social_id", "social_type"})}
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

    @Embedded
    private Introduction introduction;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<MemberThumbnail> memberThumbnails = new ArrayList<>();

    @Formula("(select count(*) from follow f where f.following_id = id)")
    private int followerCount;

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
        this.introduction = new Introduction(introduction);
    }

    public void updateProfile(Nickname nickname, Introduction introduction) {
        this.nickname = nickname;
        this.introduction = introduction;
    }

    public boolean hasNickname(Nickname nickname) {
        return Objects.equals(this.nickname, nickname);
    }

    public String getNickname() {
        return this.nickname.getValue();
    }

    public String getIntroduction() {
        return this.introduction.getValue();
    }

    public String getProfileImageUrl() {
        return Optional.ofNullable(memberThumbnails)
                .filter(thumbnails -> !thumbnails.isEmpty())
                .map(thumbnails -> thumbnails.get(0).getThumbnail().getPath())
                .orElse(null);
    }
}
