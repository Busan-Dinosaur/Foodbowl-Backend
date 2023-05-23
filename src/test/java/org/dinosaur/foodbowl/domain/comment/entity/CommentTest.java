package org.dinosaur.foodbowl.domain.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class CommentTest extends RepositoryTest {

    @Nested
    @DisplayName("댓글 작성자와 ")
    class IsBelongTo {

        @Test
        @DisplayName("검증하려는 회원이 일치하지 않으면 true를 반환한다.")
        void isBelongToFalse() {
            Member gray = memberTestSupport.memberBuilder().nickname("gray").build();
            Member hoy = memberTestSupport.memberBuilder().nickname("hoy").build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().post(post).member(gray).message("안녕하세요").build();

            assertThat(comment.isNotBelongTo(hoy)).isTrue();
        }

        @Test
        @DisplayName("검증하려는 회원이 일치하면 false를 반환한다.")
        void isBelongToTrue() {
            Member gray = memberTestSupport.memberBuilder().nickname("gray").build();
            Post post = postTestSupport.postBuilder().build();
            Comment comment = commentTestSupport.builder().post(post).member(gray).message("안녕하세요").build();

            assertThat(comment.isNotBelongTo(gray)).isFalse();
        }
    }
}
