package org.dinosaur.foodbowl.domain.review.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;

public interface ReviewPhotoCustomRepository {

    List<ReviewPhotoPathDto> getPhotoPathByReviews(List<Review> reviews);

    long deleteAllByReview(Review review);

    long deleteByReviewAndPhotos(Review review, List<Photo> photos);
}
