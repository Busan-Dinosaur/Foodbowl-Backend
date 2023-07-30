package org.dinosaur.foodbowl.domain.review.persistence;

import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.springframework.data.repository.Repository;

public interface ReviewRepository extends Repository<Review, Long> {
}
