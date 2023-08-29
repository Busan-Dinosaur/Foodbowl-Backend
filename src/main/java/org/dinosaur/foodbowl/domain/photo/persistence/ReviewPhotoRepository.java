package org.dinosaur.foodbowl.domain.photo.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.ReviewPhoto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

public interface ReviewPhotoRepository extends Repository<ReviewPhoto, Long>, ReviewPhotoCustomRepository {

    ReviewPhoto save(ReviewPhoto reviewPhoto);

    @EntityGraph(attributePaths = {"photo"})
    List<ReviewPhoto> findAllByReview(Review review);
}
