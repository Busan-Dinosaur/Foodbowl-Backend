package org.dinosaur.foodbowl.domain.photo.persistence;

import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.springframework.data.repository.Repository;

public interface ThumbnailRepository extends Repository<Thumbnail, Long> {
}
