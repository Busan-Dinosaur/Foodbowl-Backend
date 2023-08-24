package org.dinosaur.foodbowl.domain.photo.persistence;

import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.springframework.data.repository.Repository;

public interface PhotoRepository extends Repository<Photo, Long> {

    Photo save(Photo photo);
}
