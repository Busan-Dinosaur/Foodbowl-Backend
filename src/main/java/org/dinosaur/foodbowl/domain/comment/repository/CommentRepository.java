package org.dinosaur.foodbowl.domain.comment.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.data.repository.Repository;

public interface CommentRepository extends Repository<Comment, Long> {

    List<Comment> findAll();

    Comment save(Comment comment);

    void deleteAllByMember(Member member);

    void deleteAllByPost(Post post);
}
