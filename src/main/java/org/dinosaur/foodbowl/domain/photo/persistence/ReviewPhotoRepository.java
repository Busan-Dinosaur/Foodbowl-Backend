package org.dinosaur.foodbowl.domain.photo.persistence;

import org.dinosaur.foodbowl.domain.photo.domain.ReviewPhoto;
import org.springframework.data.repository.Repository;

public interface ReviewPhotoRepository extends Repository<ReviewPhoto, Long>, ReviewPhotoCustomRepository {

    ReviewPhoto save(ReviewPhoto reviewPhoto);
}
