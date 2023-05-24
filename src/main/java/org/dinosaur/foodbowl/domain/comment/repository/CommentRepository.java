package org.dinosaur.foodbowl.domain.comment.repository;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.repository.Repository;

public interface CommentRepository extends Repository<Comment, Long> {

    List<Comment> findAll();

    List<Comment> findAllByMember(Member member);

    List<Comment> findAllByPost(Post post);

    Comment save(Comment comment);

    Optional<Comment> findById(Long commentId);

    void deleteAllByMember(Member member);

    void deleteAllByPost(Post post);
}
