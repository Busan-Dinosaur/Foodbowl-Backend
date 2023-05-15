package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

public interface PostRepository extends Repository<Post, Long> {

    @EntityGraph(attributePaths = "thumbnail")
    Page<Post> findAllByMember(Member member, Pageable pageable);

    List<Post> findAll();

    Post save(Post post);

    void delete(Post post);
}
