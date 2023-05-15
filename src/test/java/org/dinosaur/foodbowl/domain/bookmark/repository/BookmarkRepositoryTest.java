package org.dinosaur.foodbowl.domain.bookmark.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookmarkRepositoryTest extends RepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Test
    @DisplayName("멤버가 등록한 북마크 목록을 삭제한다.")
    void deleteAllByMember() {
        Member member = memberTestSupport.memberBuilder().build();
        bookmarkTestSupport.builder().member(member).build();

        bookmarkRepository.deleteAllByMember(member);

        assertThat(bookmarkRepository.findAllByMember(member)).isEmpty();
    }

    @Test
    @DisplayName("게시글의 북마크 목록을 삭제한다.")
    void deleteAllByPost() {
        Post post = postTestSupport.postBuilder().build();
        bookmarkTestSupport.builder().post(post).build();

        bookmarkRepository.deleteAllByPost(post);

        assertThat(bookmarkRepository.findAllByPost(post)).isEmpty();
    }
}
