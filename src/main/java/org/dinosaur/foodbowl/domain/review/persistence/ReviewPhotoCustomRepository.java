package org.dinosaur.foodbowl.domain.review.persistence;

import static org.dinosaur.foodbowl.domain.photo.domain.QPhoto.photo;
import static org.dinosaur.foodbowl.domain.review.domain.QReviewPhoto.reviewPhoto;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.persistence.dto.QReviewPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewPhotoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public List<ReviewPhotoPathDto> findPhotoPathByReviews(List<Review> reviews) {
        return jpaQueryFactory.select(
                        new QReviewPhotoPathDto(
                                reviewPhoto.review.id,
                                photo.path
                        )
                )
                .from(reviewPhoto)
                .innerJoin(reviewPhoto.photo, photo)
                .where(reviewPhoto.review.in(reviews))
                .orderBy(reviewPhoto.id.asc())
                .fetch();
    }

    public List<ReviewPhoto> findAllReviewPhotosInReviews(List<Review> reviews) {
        return jpaQueryFactory.selectFrom(reviewPhoto)
                .innerJoin(reviewPhoto.photo, photo)
                .where(reviewPhoto.review.in(reviews))
                .fetch();
    }

    public long deleteAllByReview(Review review) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.eq(review))
                .execute();
    }

    public long deleteByReviewAndPhotos(Review review, List<Photo> photos) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.eq(review).and(reviewPhoto.photo.in(photos)))
                .execute();
    }

    public long deleteAllByReviews(List<Review> reviews) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.in(reviews))
                .execute();
    }
}
