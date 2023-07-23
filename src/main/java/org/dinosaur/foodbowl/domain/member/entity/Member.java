package org.dinosaur.foodbowl.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.common.AuditingEntity;
import org.dinosaur.foodbowl.domain.follow.entity.Follow;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.post.entity.Post;

@Getter
@Entity
@Table(name = "member")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    @Column(name = "social_type", updatable = false, length = 45)
    private SocialType socialType;

    @NotNull
    @Column(name = "social_id", updatable = false, length = 512)
    private String socialId;

    @Column(name = "email", length = 255)
    private String email;

    @NotNull
    @Column(name = "nickname", length = 45)
    private String nickname;

    @Column(name = "introduction", length = 255)
    private String introduction;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "follower")
    private List<Follow> followings = new ArrayList<>();

    @Builder
    private Member(
            Thumbnail thumbnail, SocialType socialType, String socialId,
            String email, String nickname, String introduction
    ) {
        this.thumbnail = thumbnail;
        this.socialType = socialType;
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.introduction = introduction;
    }

    public void updateProfile(final String nickname, final String introduction) {
        this.nickname = nickname;
        this.introduction = introduction;
    }

    public String getThumbnailPath() {
        return thumbnail != null ? thumbnail.getPath() : null;
    }

    public enum SocialType {

        APPLE
    }
}
