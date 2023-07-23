package org.dinosaur.foodbowl.domain.review.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(name = "review_thumbnail")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewThumbnail extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, updatable = false)
    private Review review;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id", nullable = false, updatable = false)
    private Thumbnail thumbnail;

    @Builder
    private ReviewThumbnail(Review review, Thumbnail thumbnail) {
        this.review = review;
        this.thumbnail = thumbnail;
    }
}
