package org.dinosaur.foodbowl.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.STORE_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequest;
import org.dinosaur.foodbowl.domain.post.dto.response.PostStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.dinosaur.foodbowl.testsupport.fixture.MultipartFileFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

class PostServiceTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Test
    @DisplayName("최근 게시글 썸네일 목록을 조회한다.")
    void findLatestThumbnails() {
        Post postA = postTestSupport.postBuilder().build();
        Post postB = postTestSupport.postBuilder().build();
        Post postC = postTestSupport.postBuilder().build();

        Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.DESC, "id"));
        PageResponse<PostThumbnailResponse> result = postService.findLatestThumbnails(pageable);

        List<PostThumbnailResponse> response = result.getContent();
        assertAll(
                () -> assertThat(response).hasSize(3),
                () -> assertThat(response.get(0).getPostId()).isEqualTo(postC.getId()),
                () -> assertThat(response.get(1).getPostId()).isEqualTo(postB.getId()),
                () -> assertThat(response.get(2).getPostId()).isEqualTo(postA.getId()),
                () -> assertThat(result.isFirst()).isTrue(),
                () -> assertThat(result.isLast()).isTrue(),
                () -> assertThat(result.isHasNext()).isFalse(),
                () -> assertThat(result.getCurrentPageIndex()).isEqualTo(0),
                () -> assertThat(result.getCurrentElementSize()).isEqualTo(5),
                () -> assertThat(result.getTotalPage()).isEqualTo(1),
                () -> assertThat(result.getTotalElementCount()).isEqualTo(3)
        );
    }

    @Nested
    @DisplayName("save 메서드는 ")
    class Save {

        @Test
        @DisplayName("게시글 생성시 생성된 ID를 반환한다.")
        void success() {
            Member member = memberTestSupport.memberBuilder().build();
            Store store = storeTestSupport.builder().build();

            PostCreateRequest request = new PostCreateRequest(store.getId(), List.of("카페", "한식"), "유효한 게시글");

            Long postId = postService.save(member.getId(), request, List.of(MultipartFileFixture.get()));

            assertThat(postId).isNotNull();
        }

        @Test
        @DisplayName("memberId가 존재하지 않는다면 BadRequest를 반환한다.")
        void fail_invalid_memberId() {
            Store store = storeTestSupport.builder().build();

            PostCreateRequest request = new PostCreateRequest(store.getId(), List.of("카페", "한식"), "유효한 게시글");

            assertThatThrownBy(() -> postService.save(-1L, request, List.of(MultipartFileFixture.get())))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(MEMBER_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("storeId가 존재하지 않는다면 BadRequest를 반환한다.")
        void fail_invalid_storeId() {
            Member member = memberTestSupport.memberBuilder().build();

            PostCreateRequest request = new PostCreateRequest(-1L, List.of("카페", "한식"), "유효한 게시글");

            assertThatThrownBy(() -> postService.save(member.getId(), request, List.of(MultipartFileFixture.get())))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage(STORE_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("findThumbnailsInProfile 메서드는 ")
    class FindThumbnailsInProfile {

        @Test
        @DisplayName("프로필의 게시글 썸네일 목록을 조회한다.")
        void findThumbnailsInProfile() {
            Member member = memberTestSupport.memberBuilder().build();
            Post post = postTestSupport.postBuilder().member(member).build();

            Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.DESC, "createdAt"));
            PageResponse<PostThumbnailResponse> result = postService.findThumbnailsInProfile(member.getId(), pageable);

            assertAll(
                    () -> assertThat(result.getContent()).hasSize(1),
                    () -> assertThat(result.getContent().get(0).getPostId()).isEqualTo(post.getId())
            );
        }

        @Test
        @DisplayName("존재하지 않는 멤버라면 예외를 던진다.")
        void findThumbnailsInProfileWithInvalidMember() {
            Pageable pageable = PageRequest.of(0, 5, Sort.by(Direction.DESC, "createdAt"));
            assertThatThrownBy(() -> postService.findThumbnailsInProfile(1L, pageable))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Nested
    @DisplayName("findPostStoreMarkers 메서드는 ")
    class FindPostStoreMarkers {

        @Test
        @DisplayName("등록되지 않는 멤버라면 예외를 던진다.")
        void unregisteredMember() {
            Long memberId = -1L;

            assertThatThrownBy(() -> postService.findPostStoreMarkers(memberId))
                    .isInstanceOf(FoodbowlException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        @DisplayName("가게 중복이 발생하지 않는다.")
        void noDuplicationStore() {
            Member member = memberTestSupport.memberBuilder().build();
            Store store = storeTestSupport.builder().build();
            Post postA = postTestSupport.postBuilder().member(member).store(store).build();
            postTestSupport.postBuilder().member(member).store(store).build();

            List<PostStoreMarkerResponse> result = postService.findPostStoreMarkers(member.getId());

            List<PostStoreMarkerResponse> expected = List.of(PostStoreMarkerResponse.from(postA.getStore()));
            assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("지도에 마킹하기 위해 멤버가 작성한 게시글 가게 정보 목록을 조회한다.")
        void findListOfStoreInformationForMarkingOnTheMap() {
            Member member = memberTestSupport.memberBuilder().build();
            Post postA = postTestSupport.postBuilder().member(member).build();
            Post postB = postTestSupport.postBuilder().member(member).build();

            List<PostStoreMarkerResponse> result = postService.findPostStoreMarkers(member.getId());

            List<PostStoreMarkerResponse> expected = List.of(
                    PostStoreMarkerResponse.from(postA.getStore()),
                    PostStoreMarkerResponse.from(postB.getStore())
            );
            assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        }
    }
}
