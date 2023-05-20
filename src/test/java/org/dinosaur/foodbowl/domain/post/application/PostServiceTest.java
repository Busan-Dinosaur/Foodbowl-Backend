package org.dinosaur.foodbowl.domain.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

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
