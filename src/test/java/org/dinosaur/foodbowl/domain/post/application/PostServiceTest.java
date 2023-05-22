package org.dinosaur.foodbowl.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
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
                () -> assertThat(result.getCurrentPage()).isEqualTo(0),
                () -> assertThat(result.getCurrentElementSize()).isEqualTo(5),
                () -> assertThat(result.getTotalPage()).isEqualTo(1),
                () -> assertThat(result.getTotalElementSize()).isEqualTo(3)
        );
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
}
