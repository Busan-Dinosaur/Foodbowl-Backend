package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.repository.Repository;

public interface PostRepository extends Repository<Post, Long> {

    Optional<Post> findById(Long id);

    List<Post> findAll();

    Post save(Post post);

    void delete(Post post);
}
