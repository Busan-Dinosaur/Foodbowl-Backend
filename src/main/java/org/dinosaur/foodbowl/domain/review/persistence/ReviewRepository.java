package org.dinosaur.foodbowl.domain.review.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.springframework.data.repository.Repository;

public interface ReviewRepository extends Repository<Review, Long> {

    Optional<Review> findById(Long id);

    Review save(Review review);

    void delete(Review review);
}
