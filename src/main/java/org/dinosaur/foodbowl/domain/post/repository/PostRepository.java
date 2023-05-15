package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.repository.Repository;

public interface PostRepository extends Repository<Post, Long> {

    List<Post> findAll();

    Post save(Post post);

    void delete(Post post);
}
