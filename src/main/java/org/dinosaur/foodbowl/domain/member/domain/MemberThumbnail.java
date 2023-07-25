package org.dinosaur.foodbowl.domain.member.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(
        name = "member_thumbnail",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_MEMBER_THUMBNAIL", columnNames = {"member_id", "thumbnail_id"})
        }
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberThumbnail extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id")
    private Thumbnail thumbnail;

    @Builder
    private MemberThumbnail(Member member, Thumbnail thumbnail) {
        this.member = member;
        this.thumbnail = thumbnail;
    }
}
