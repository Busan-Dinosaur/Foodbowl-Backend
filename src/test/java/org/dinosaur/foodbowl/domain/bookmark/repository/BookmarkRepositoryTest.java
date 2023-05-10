package org.dinosaur.foodbowl.domain.bookmark.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
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

        assertThat(bookmarkRepository.findAll()).isEmpty();
    }
}
