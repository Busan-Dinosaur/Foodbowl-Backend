package org.dinosaur.foodbowl.domain.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.COMMENT_NOT_FOUND;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.COMMENT_UNAUTHORIZED;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.POST_NOT_FOUND;

import jakarta.persistence.EntityManager;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.comment.repository.CommentRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends IntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    @Nested
    @DisplayName("게시글에 ")
    class Save {

        @Test
        @DisplayName("댓글을 추가한다.")
        void save() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            CommentCreateRequest commentCreateRequest = new CommentCreateRequest(post.getId(), "안녕하세요.");

            Long commentId = commentService.save(member.getId(), commentCreateRequest);

            assertThat(commentId).isPositive();
        }

        @Test
        @DisplayName("댓글을 추가할 때, 게시글이 존재하지 않으면 예외가 발생한다.")
        void saveFailWithWrongPostId() {
            Member member = memberTestSupport.memberBuilder().build();
            postTestSupport.postBuilder().build();
            CommentCreateRequest commentCreateRequest = new CommentCreateRequest(Long.MAX_VALUE, "안녕하세요.");

            assertThatThrownBy(() -> commentService.save(member.getId(), commentCreateRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(POST_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글을 추가할 때, 회원이 존재하지 않으면 예외가 발생한다.")
        void saveFailWithWrongMemberId() {
            memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            CommentCreateRequest commentCreateRequest = new CommentCreateRequest(post.getId(), "안녕하세요.");

            assertThatThrownBy(() -> commentService.save(Long.MAX_VALUE, commentCreateRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글을 수정할 때 ")
    class UpdateComment {

        @Test
        @DisplayName("정상적으로 내용이 수정된다.")
        void updateComment() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().member(member).post(post).message("돈까스 드시죠").build();
            CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("아, 돈까스 말고 그냥 국밥 드시죠");
            em.flush();
            em.clear();

            commentService.updateComment(comment.getId(), member.getId(), commentUpdateRequest);
            em.flush();
            em.clear();
            Comment updatedComment = commentRepository.findById(comment.getId()).get();

            assertThat(updatedComment.getMessage()).isEqualTo(commentUpdateRequest.getMessage());
        }

        @Test
        @DisplayName("댓글이 존재하지 않는 경우 예외가 발생한다.")
        void updateCommentFailWithWrongCommentId() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            commentTestSupport.builder().member(member).post(post).message("돈까스 드시죠").build();
            CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("아, 돈까스 말고 그냥 국밥 드시죠");

            assertThatThrownBy(() -> commentService.updateComment(-1L, member.getId(), commentUpdateRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("요청 회원이 존재하지 않는 경우 예외가 발생한다.")
        void updateCommentFailWithWrongMemberId() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().member(member).post(post).message("오늘 남으시나요?").build();
            CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("오늘도 칼퇴 하시나요?");

            assertThatThrownBy(() -> commentService.updateComment(comment.getId(), -1L, commentUpdateRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 작성자와 댓글 수정 요청자가 다르면 예외가 발생한다.")
        void updateCommentFailWithDifferentAuthor() {
            Member gray = memberTestSupport.memberBuilder().build();
            Member dazzle = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().member(gray).post(post).message("아 날씨 좋다").build();
            CommentUpdateRequest commentUpdateRequest = new CommentUpdateRequest("아 내일 비온다.");

            assertThatThrownBy(
                    () -> commentService.updateComment(comment.getId(), dazzle.getId(), commentUpdateRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(COMMENT_UNAUTHORIZED.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글을 삭제하면 ")
    class DeleteComment {

        @Test
        @DisplayName("정상적으로 삭제된다.")
        void deleteComment() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().member(member).post(post).message("댓글 생성").build();

            commentService.deleteComment(comment.getId(), member.getId());

            assertThat(commentRepository.findById(comment.getId())).isEmpty();
        }

        @Test
        @DisplayName("댓글이 존재하지 않는 경우 예외가 발생한다.")
        void deleteCommentFailWithWrongCommentId() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            commentTestSupport.builder().member(member).post(post).message("돈까스 드시죠").build();

            assertThatThrownBy(() -> commentService.deleteComment(-1L, member.getId()))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("요청 회원이 존재하지 않는 경우 예외가 발생한다.")
        void deleteCommentFailWithWrongMemberId() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().member(member).post(post).message("오늘 남으시나요?").build();

            assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), -1L))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("댓글 작성자와 댓글 삭제 요청자가 다르면 예외가 발생한다.")
        void deleteCommentFailWithDifferentAuthor() {
            Member gray = memberTestSupport.memberBuilder().build();
            Member dazzle = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().member(gray).post(post).message("아 날씨 좋다").build();

            assertThatThrownBy(
                    () -> commentService.deleteComment(comment.getId(), dazzle.getId()))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(COMMENT_UNAUTHORIZED.getMessage());
        }
    }
}
