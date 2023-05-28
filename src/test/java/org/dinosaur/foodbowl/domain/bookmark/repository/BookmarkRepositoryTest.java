package org.dinosaur.foodbowl.domain.bookmark.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

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

    @Test
    @DisplayName("엔티티 그래프, 멤버, 페이징 정보를 바탕으로 북마크를 조회한다.")
    void findAllByMemberWithEntityGraph() {
        Post oldPost = postTestSupport.postBuilder().build();
        Post newPost = postTestSupport.postBuilder().build();
        Member member = memberTestSupport.memberBuilder().build();
        bookmarkTestSupport.builder().member(member).post(oldPost).build();
        bookmarkTestSupport.builder().member(member).post(newPost).build();

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Direction.DESC, "id"));
        Page<Bookmark> result = bookmarkRepository.findAllByMember(member, pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.hasNext()).isTrue(),
                () -> assertThat(result.getTotalElements()).isEqualTo(2),
                () -> assertThat(result.getTotalPages()).isEqualTo(2)
        );
    }

    @Nested
    @DisplayName("findWithPostAndStoreAllByMember 메서드는 ")
    class FindWithPostAndStoreAllByMember {

        @Test
        @DisplayName("해당 멤버의 북마크만 조회한다.")
        void findWithPostAndStoreAllOnlyMember() {
            Member member = memberTestSupport.memberBuilder().build();
            bookmarkTestSupport.builder().build();

            List<Bookmark> result = bookmarkRepository.findWithPostAndStoreAllByMember(member);

            assertThat(result).hasSize(0);
        }

        @Test
        @DisplayName("해당 멤버의 북마크를 모두 조회한다.")
        void findWithPostAndStoreAllMember() {
            Member member = memberTestSupport.memberBuilder().build();
            bookmarkTestSupport.builder().member(member).build();
            bookmarkTestSupport.builder().member(member).build();

            List<Bookmark> result = bookmarkRepository.findWithPostAndStoreAllByMember(member);

            assertThat(result).hasSize(2);
        }
    }
}
