package org.dinosaur.foodbowl.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;

public interface PostRepository extends Repository<Post, Long> {

    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = "store")
    List<Post> findWithStoreAllByMember(Member member);

    @EntityGraph(attributePaths = "thumbnail")
    Page<Post> findAllByMember(Member member, Pageable pageable);

    List<Post> findAll();

    Post save(Post post);

    void delete(Post post);
}
