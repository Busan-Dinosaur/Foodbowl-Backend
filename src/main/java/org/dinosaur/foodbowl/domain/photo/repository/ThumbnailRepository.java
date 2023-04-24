package org.dinosaur.foodbowl.domain.photo.repository;

import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.springframework.data.repository.Repository;

public interface ThumbnailRepository extends Repository<Thumbnail, Long> {

    Thumbnail save(Thumbnail thumbnail);
}
