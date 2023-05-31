package org.dinosaur.foodbowl.domain.photo.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.entity.Photo;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.repository.Repository;

public interface PhotoRepository extends Repository<Photo, Long> {

    List<Photo> findAll();

    Photo save(Photo photo);

    List<Photo> saveAll(Iterable<Photo> photos);

    void deleteAllByPost(Post post);
}
