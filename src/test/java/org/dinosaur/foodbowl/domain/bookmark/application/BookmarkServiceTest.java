package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class BookmarkServiceTest extends IntegrationTest {

    @Autowired
    private BookmarkService bookmarkService;

    @Nested
    @DisplayName("findThumbnailsInProfile 메서드는 ")
    class FindThumbnailsInProfile {

        @Test
        @DisplayName("멤버가 존재하지 않으면 예외를 던진다.")
        void findThumbnailsInProfileWithInvalidMember() {
            Pageable pageable = PageRequest.of(0, 5);

            assertThatThrownBy(() -> bookmarkService.findThumbnailsInProfile(-1L, pageable))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        @DisplayName("멤버의 북마크 페이징 목록을 조회한다.")
        void findThumbnailsInProfile() {
            Post oldPost = postTestSupport.postBuilder().build();
            Post newPost = postTestSupport.postBuilder().build();
            Member member = memberTestSupport.memberBuilder().build();
            bookmarkTestSupport.builder().member(member).post(oldPost).build();
            bookmarkTestSupport.builder().member(member).post(newPost).build();

            Pageable pageable = PageRequest.of(0, 5);
            PageResponse<BookmarkThumbnailResponse> result =
                    bookmarkService.findThumbnailsInProfile(member.getId(), pageable);

            assertAll(
                    () -> assertThat(result.getContent()).hasSize(2),
                    () -> assertThat(result.isLast()).isTrue(),
                    () -> assertThat(result.isHasNext()).isFalse()
            );
        }
    }
}
