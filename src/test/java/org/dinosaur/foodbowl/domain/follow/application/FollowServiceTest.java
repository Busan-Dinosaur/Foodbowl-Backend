package org.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowResponse;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.common.response.PageResponse;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowServiceTest extends IntegrationTest {

    @Autowired
    private FollowService followService;

    @Autowired
    private FollowRepository followRepository;

    @Nested
    class 팔로워_목록_조회 {

        @Test
        void 프로필_존재하지_않는_팔로워_목록을_조회한다() {
            Member following = memberTestPersister.memberBuilder().save();
            Member followerA = memberTestPersister.memberBuilder().save();
            Member followerB = memberTestPersister.memberBuilder().save();

            Follow followA = followTestPersister.builder().following(following).follower(followerA).save();
            Follow followB = followTestPersister.builder().following(following).follower(followerB).save();

            PageResponse<FollowResponse> response = followService.getFollowers(0, 2, following);

            assertSoftly(
                    softly -> {
                        softly.assertThat(response.content())
                                .usingRecursiveComparison()
                                .isEqualTo(
                                        List.of(
                                                FollowResponse.from(followB.getFollower()),
                                                FollowResponse.from(followA.getFollower())
                                        )
                                );
                        softly.assertThat(response.isFirst()).isTrue();
                        softly.assertThat(response.isLast()).isTrue();
                        softly.assertThat(response.hasNext()).isFalse();
                        softly.assertThat(response.currentPage()).isEqualTo(0);
                        softly.assertThat(response.currentSize()).isEqualTo(2);
                    }
            );
        }

        @Test
        void 프로필_존재하는_팔로워_목록을_조회한다() {
            Member following = memberTestPersister.memberBuilder().save();
            Member followerA = memberTestPersister.memberBuilder().save();
            Member followerB = memberTestPersister.memberBuilder().save();

            memberTestPersister.memberThumbnailBuilder().member(followerA).save();
            memberTestPersister.memberThumbnailBuilder().member(followerB).save();

            followTestPersister.builder().following(following).follower(followerA).save();
            followTestPersister.builder().following(following).follower(followerB).save();

            PageResponse<FollowResponse> response = followService.getFollowers(0, 2, following);

            assertSoftly(
                    softly -> {
                        softly.assertThat(response.content()).hasSize(2);
                        softly.assertThat(response.isFirst()).isTrue();
                        softly.assertThat(response.isLast()).isTrue();
                        softly.assertThat(response.hasNext()).isFalse();
                        softly.assertThat(response.currentPage()).isEqualTo(0);
                        softly.assertThat(response.currentSize()).isEqualTo(2);
                    }
            );
        }
    }

    @Nested
    class 팔로우_등록 {

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> followService.follow(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 자신을_팔로우한다면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> followService.follow(loginMember.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인을 팔로우할 수 없습니다.");
        }

        @Test
        void 이미_팔로우한_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member other = memberTestPersister.memberBuilder().save();
            followTestPersister.builder()
                    .following(other)
                    .follower(loginMember)
                    .save();

            assertThatThrownBy(() -> followService.follow(other.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 팔로우한 회원입니다.");
        }

        @Test
        void 유효한_상황이라면_팔로우한다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member other = memberTestPersister.memberBuilder().save();

            followService.follow(other.getId(), loginMember);

            Optional<Follow> follow = followRepository.findByFollowingAndFollower(other, loginMember);
            assertThat(follow).isPresent();
        }
    }

    @Nested
    class 팔로우_취소 {

        @Test
        void 등록되지_않은_회원이라면_에외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> followService.unfollow(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 팔로우_하지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member followMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> followService.unfollow(followMember.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("팔로우 하지 않은 회원입니다.");
        }

        @Test
        void 유효한_상황이라면_언팔로우한다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member followMember = memberTestPersister.memberBuilder().save();
            Follow follow = followTestPersister.builder()
                    .following(followMember)
                    .follower(loginMember)
                    .save();

            followService.unfollow(followMember.getId(), loginMember);

            assertThat(followRepository.findById(follow.getId())).isNotPresent();
        }
    }

    @Nested
    class 팔로워_삭제 {

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> followService.deleteFollower(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 나를_팔로우하지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member followMember = memberTestPersister.memberBuilder().save();

            assertThatThrownBy(() -> followService.deleteFollower(followMember.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("나를 팔로우 하지 않은 회원입니다.");
        }

        @Test
        void 유효한_상황이라면_팔로워를_삭제한다() {
            Member loginMember = memberTestPersister.memberBuilder().save();
            Member followMember = memberTestPersister.memberBuilder().save();
            Follow follow = followTestPersister.builder()
                    .following(loginMember)
                    .follower(followMember)
                    .save();

            followService.deleteFollower(followMember.getId(), loginMember);

            assertThat(followRepository.findById(follow.getId())).isNotPresent();
        }
    }
}