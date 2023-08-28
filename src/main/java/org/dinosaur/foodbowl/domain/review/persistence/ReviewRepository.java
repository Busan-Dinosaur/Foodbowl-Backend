package org.dinosaur.foodbowl.domain.review.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.springframework.data.repository.Repository;

public interface ReviewRepository extends Repository<Review, Long> {

    Review save(Review review);

    Optional<Review> findById(Long id);

    void delete(Review review);
}
