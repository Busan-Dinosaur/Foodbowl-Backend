package org.dinosaur.foodbowl.domain.photo.persistence;

import org.dinosaur.foodbowl.domain.review.domain.Review;

public interface ReviewPhotoCustomRepository {

    long deleteAllByReview(Review review);
}
