package org.dinosaur.foodbowl.domain.comment.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends IntegrationTest {

    @Autowired
    CommentService commentService;

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
                    .hasMessage("게시글이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("댓글을 추가할 때, 회원이 존재하지 않으면 예외가 발생한다.")
        void saveFailWithWrongMemberId() {
            memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().build();
            CommentCreateRequest commentCreateRequest = new CommentCreateRequest(post.getId(), "안녕하세요.");

            assertThatThrownBy(() -> commentService.save(Long.MAX_VALUE, commentCreateRequest))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }
}
