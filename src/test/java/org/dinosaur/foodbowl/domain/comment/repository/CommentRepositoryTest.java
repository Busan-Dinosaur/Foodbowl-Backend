package org.dinosaur.foodbowl.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentRepositoryTest extends RepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("댓글을 저장한다.")
    void createComment() {
        Post post = postTestSupport.postBuilder().build();
        Member member = memberTestSupport.memberBuilder().build();
        Comment comment = Comment.builder()
                .post(post)
                .member(member)
                .parent(null)
                .message("정답은 없다. 자신만의 기준을 가지면 된다.")
                .build();

        Comment savedComment = commentRepository.save(comment);

        assertAll(
                () -> assertThat(savedComment.getId()).isNotNull(),
                () -> assertThat(savedComment.getMember()).isEqualTo(comment.getMember()),
                () -> assertThat(savedComment.getPost()).isEqualTo(comment.getPost()),
                () -> assertThat(savedComment.getParent()).isNull(),
                () -> assertThat(savedComment.getMessage()).isEqualTo(comment.getMessage())
        );
    }

    @Test
    @DisplayName("멤버가 작성한 댓글 목록을 삭제한다.")
    void deleteAllByMember() {
        Member member = memberTestSupport.memberBuilder().build();
        commentTestSupport.builder().member(member).build();

        commentRepository.deleteAllByMember(member);

        assertThat(commentRepository.findAllByMember(member)).isEmpty();
    }

    @Test
    @DisplayName("게시글에 작성된 댓글 목록을 삭제한다.")
    void deleteAllByPost() {
        Post post = postTestSupport.postBuilder().build();
        commentTestSupport.builder().post(post).build();

        commentRepository.deleteAllByPost(post);

        assertThat(commentRepository.findAllByPost(post)).isEmpty();
    }
}
