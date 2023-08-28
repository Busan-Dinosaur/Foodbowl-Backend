package org.dinosaur.foodbowl.domain.photo.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.springframework.data.repository.Repository;

public interface PhotoRepository extends Repository<Photo, Long>, PhotoCustomRepository {

    Photo save(Photo photo);

    Optional<Photo> findById(Long id);
}
