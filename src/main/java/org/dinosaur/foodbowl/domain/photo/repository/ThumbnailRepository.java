package org.dinosaur.foodbowl.domain.photo.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.springframework.data.repository.Repository;

public interface ThumbnailRepository extends Repository<Thumbnail, Long> {

    List<Thumbnail> findAll();

    Thumbnail save(Thumbnail thumbnail);

    void delete(Thumbnail thumbnail);
}
