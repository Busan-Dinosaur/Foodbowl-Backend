package org.dinosaur.foodbowl.domain.review.persistence;

import static org.dinosaur.foodbowl.domain.photo.domain.QPhoto.photo;
import static org.dinosaur.foodbowl.domain.review.domain.QReviewPhoto.reviewPhoto;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.dto.QReviewPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewPhotoCustomRepositoryImpl implements ReviewPhotoCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReviewPhotoPathDto> getPhotoPathByReviews(List<Review> reviews) {
        return jpaQueryFactory.select(
                        new QReviewPhotoPathDto(
                                reviewPhoto.review.id,
                                photo.path
                        )
                )
                .from(reviewPhoto)
                .innerJoin(reviewPhoto.photo, photo)
                .where(reviewPhoto.review.in(reviews))
                .fetch();
    }

    @Override
    public long deleteAllByReview(Review review) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.eq(review))
                .execute();
    }

    @Override
    public long deleteByReviewAndPhotos(Review review, List<Photo> photos) {
        return jpaQueryFactory.delete(reviewPhoto)
                .where(reviewPhoto.review.eq(review).and(reviewPhoto.photo.in(photos)))
                .execute();
    }
}
