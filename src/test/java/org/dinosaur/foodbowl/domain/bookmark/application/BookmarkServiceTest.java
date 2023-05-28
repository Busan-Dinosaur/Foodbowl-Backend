package org.dinosaur.foodbowl.domain.bookmark.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.dto.response.PostStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.store.entity.Store;
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

    @Nested
    @DisplayName("findBookmarkStoreMarkers 메서드는 ")
    class FindBookmarkStoreMarkers {

        @Test
        @DisplayName("해당 멤버가 존재하지 않으면 예외를 던진다.")
        void notExistMember() {
            assertThatThrownBy(() -> bookmarkService.findBookmarkStoreMarkers(-1L))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        @DisplayName("가게 중복이 발생하지 않는다.")
        void noDuplicationStore() {
            Member member = memberTestSupport.memberBuilder().build();
            Store store = storeTestSupport.builder().build();
            Post postA = postTestSupport.postBuilder().store(store).build();
            Post postB = postTestSupport.postBuilder().store(store).build();
            Bookmark bookmarkA = bookmarkTestSupport.builder().member(member).post(postA).build();
            bookmarkTestSupport.builder().member(member).post(postB).build();

            List<BookmarkStoreMarkerResponse> result = bookmarkService.findBookmarkStoreMarkers(member.getId());

            List<PostStoreMarkerResponse> expected = List.of(PostStoreMarkerResponse.from(bookmarkA.getPost().getStore()));
            assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("북마크 게시글의 가게 위치 기반 데이터 목록을 반환한다.")
        void getBookmarkPostStoreForLocation() {
            Member member = memberTestSupport.memberBuilder().build();
            Post postA = postTestSupport.postBuilder().build();
            Post postB = postTestSupport.postBuilder().build();
            Bookmark bookmarkA = bookmarkTestSupport.builder().member(member).post(postA).build();
            Bookmark bookmarkB = bookmarkTestSupport.builder().member(member).post(postB).build();

            List<BookmarkStoreMarkerResponse> result = bookmarkService.findBookmarkStoreMarkers(member.getId());

            List<BookmarkStoreMarkerResponse> expected = List.of(
                    BookmarkStoreMarkerResponse.from(bookmarkA.getPost().getStore()),
                    BookmarkStoreMarkerResponse.from(bookmarkB.getPost().getStore())
            );
            assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        }
    }
}
