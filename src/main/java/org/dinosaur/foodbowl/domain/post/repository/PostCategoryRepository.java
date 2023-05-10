package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.entity.PostCategory;
import org.springframework.data.repository.Repository;

public interface PostCategoryRepository extends Repository<PostCategory, Long> {

    List<PostCategory> findAll();

    PostCategory save(PostCategory postCategory);

    void deleteAllByPost(Post post);
}
