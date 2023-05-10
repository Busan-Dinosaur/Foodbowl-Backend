package org.dinosaur.foodbowl.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentRepositoryTest extends RepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("멤버가 작성한 댓글 목록을 삭제한다.")
    void deleteAllByMember() {
        Member member = memberTestSupport.memberBuilder().build();
        commentTestSupport.builder().member(member).build();

        commentRepository.deleteAllByMember(member);

        assertThat(commentRepository.findAll()).isEmpty();
    }
}
