package org.dinosaur.foodbowl.domain.review.domain;

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
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.global.persistence.AuditingEntity;

@Getter
@Entity
@Table(
        name = "review_photo",
        uniqueConstraints = {@UniqueConstraint(name = "uniq_review_photo", columnNames = {"review_id", "photo_id"})}
)
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewPhoto extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", updatable = false)
    private Photo photo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", updatable = false)
    private Review review;

    @Builder
    private ReviewPhoto(Photo photo, Review review) {
        this.photo = photo;
        this.review = review;
    }
}
