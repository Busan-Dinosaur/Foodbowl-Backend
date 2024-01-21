package org.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowingResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowingResponse;
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

    @Test
    void 나의_팔로잉_목록을_페이징_조회한다() {
        Member follower = memberTestPersister.builder().save();
        Member followingA = memberTestPersister.builder().save();
        Member followingB = memberTestPersister.builder().save();

        Follow followA = followTestPersister.builder().following(followingA).follower(follower).save();
        Follow followB = followTestPersister.builder().following(followingB).follower(follower).save();

        PageResponse<FollowingResponse> response = followService.getFollowings(0, 2, follower);

        assertSoftly(softly -> {
            softly.assertThat(response.content())
                    .usingRecursiveComparison()
                    .isEqualTo(
                            List.of(
                                    FollowingResponse.from(followB.getFollowing()),
                                    FollowingResponse.from(followA.getFollowing())
                            )
                    );
            softly.assertThat(response.isFirst()).isTrue();
            softly.assertThat(response.isLast()).isTrue();
            softly.assertThat(response.hasNext()).isFalse();
            softly.assertThat(response.currentPage()).isEqualTo(0);
            softly.assertThat(response.currentSize()).isEqualTo(2);
        });
    }

    @Nested
    class 다른_회원_팔로잉_목록_페이징_조회 {

        @Test
        void 유효한_상황이라면_팔로잉_목록을_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member follower = memberTestPersister.builder().save();
            Member followingA = memberTestPersister.builder().save();
            Member followingB = memberTestPersister.builder().save();

            followTestPersister.builder().following(member).follower(follower).save();
            followTestPersister.builder().following(followingA).follower(follower).save();
            followTestPersister.builder().following(followingB).follower(follower).save();
            followTestPersister.builder().following(followingA).follower(member).save();

            PageResponse<OtherUserFollowingResponse> response =
                    followService.getOtherUserFollowings(follower.getId(), 0, 10, member);

            assertSoftly(softly -> {
                softly.assertThat(response.content()).hasSize(3);
                softly.assertThat(response.content().get(0).memberId()).isEqualTo(followingB.getId());
                softly.assertThat(response.content().get(0).nickname()).isEqualTo(followingB.getNickname());
                softly.assertThat(response.content().get(0).isFollowing()).isFalse();
                softly.assertThat(response.content().get(0).isMe()).isFalse();
                softly.assertThat(response.content().get(1).memberId()).isEqualTo(followingA.getId());
                softly.assertThat(response.content().get(1).nickname()).isEqualTo(followingA.getNickname());
                softly.assertThat(response.content().get(1).isFollowing()).isTrue();
                softly.assertThat(response.content().get(1).isMe()).isFalse();
                softly.assertThat(response.content().get(2).memberId()).isEqualTo(member.getId());
                softly.assertThat(response.content().get(2).nickname()).isEqualTo(member.getNickname());
                softly.assertThat(response.content().get(2).isFollowing()).isFalse();
                softly.assertThat(response.content().get(2).isMe()).isTrue();
                softly.assertThat(response.isFirst()).isTrue();
                softly.assertThat(response.isLast()).isTrue();
                softly.assertThat(response.hasNext()).isFalse();
                softly.assertThat(response.currentPage()).isEqualTo(0);
                softly.assertThat(response.currentSize()).isEqualTo(10);
            });
        }

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.getOtherUserFollowings(-1L, 0, 2, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Test
    void 나의_팔로워_목록을_페이징_조회한다() {
        Member following = memberTestPersister.builder().save();
        Member followerA = memberTestPersister.builder().save();
        Member followerB = memberTestPersister.builder().save();

        Follow followA = followTestPersister.builder().following(following).follower(followerA).save();
        Follow followB = followTestPersister.builder().following(following).follower(followerB).save();

        PageResponse<FollowerResponse> response = followService.getFollowers(0, 2, following);

        assertSoftly(softly -> {
            softly.assertThat(response.content())
                    .usingRecursiveComparison()
                    .isEqualTo(
                            List.of(
                                    FollowerResponse.from(followB.getFollower()),
                                    FollowerResponse.from(followA.getFollower())
                            )
                    );
            softly.assertThat(response.isFirst()).isTrue();
            softly.assertThat(response.isLast()).isTrue();
            softly.assertThat(response.hasNext()).isFalse();
            softly.assertThat(response.currentPage()).isEqualTo(0);
            softly.assertThat(response.currentSize()).isEqualTo(2);
        });
    }

    @Nested
    class 다른_회원_팔로워_목록_페이징_조회 {

        @Test
        void 유효한_상황이라면_팔로워_목록을_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member following = memberTestPersister.builder().save();
            Member followerA = memberTestPersister.builder().save();
            Member followerB = memberTestPersister.builder().save();

            followTestPersister.builder().following(following).follower(followerA).save();
            followTestPersister.builder().following(following).follower(followerB).save();
            followTestPersister.builder().following(followerA).follower(member).save();

            PageResponse<OtherUserFollowerResponse> response =
                    followService.getOtherUserFollowers(following.getId(), 0, 2, member);

            assertSoftly(softly -> {
                softly.assertThat(response.content()).hasSize(2);
                softly.assertThat(response.content().get(0).memberId()).isEqualTo(followerB.getId());
                softly.assertThat(response.content().get(0).nickname()).isEqualTo(followerB.getNickname());
                softly.assertThat(response.content().get(0).isFollowing()).isFalse();
                softly.assertThat(response.content().get(1).memberId()).isEqualTo(followerA.getId());
                softly.assertThat(response.content().get(1).nickname()).isEqualTo(followerA.getNickname());
                softly.assertThat(response.content().get(1).isFollowing()).isTrue();
                softly.assertThat(response.isFirst()).isTrue();
                softly.assertThat(response.isLast()).isTrue();
                softly.assertThat(response.hasNext()).isFalse();
                softly.assertThat(response.currentPage()).isEqualTo(0);
                softly.assertThat(response.currentSize()).isEqualTo(2);
            });
        }

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.getOtherUserFollowers(-1L, 0, 2, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }
    }

    @Nested
    class 팔로우_등록 {

        @Test
        void 유효한_상황이라면_팔로우한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member other = memberTestPersister.builder().save();

            followService.follow(other.getId(), loginMember);

            Optional<Follow> follow = followRepository.findByFollowingAndFollower(other, loginMember);
            assertThat(follow).isPresent();
        }

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.follow(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 자신을_팔로우한다면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.follow(loginMember.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("본인을 팔로우할 수 없습니다.");
        }

        @Test
        void 이미_팔로우한_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();
            Member other = memberTestPersister.builder().save();
            followTestPersister.builder()
                    .following(other)
                    .follower(loginMember)
                    .save();

            assertThatThrownBy(() -> followService.follow(other.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 팔로우한 회원입니다.");
        }
    }

    @Nested
    class 팔로우_취소 {

        @Test
        void 유효한_상황이라면_언팔로우한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member followMember = memberTestPersister.builder().save();
            Follow follow = followTestPersister.builder()
                    .following(followMember)
                    .follower(loginMember)
                    .save();

            followService.unfollow(followMember.getId(), loginMember);

            assertThat(followRepository.findById(follow.getId())).isNotPresent();
        }

        @Test
        void 등록되지_않은_회원이라면_에외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.unfollow(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 팔로우_하지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();
            Member followMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.unfollow(followMember.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("팔로우 하지 않은 회원입니다.");
        }
    }

    @Nested
    class 팔로워_삭제 {

        @Test
        void 유효한_상황이라면_팔로워를_삭제한다() {
            Member loginMember = memberTestPersister.builder().save();
            Member followMember = memberTestPersister.builder().save();
            Follow follow = followTestPersister.builder()
                    .following(loginMember)
                    .follower(followMember)
                    .save();

            followService.deleteFollower(followMember.getId(), loginMember);

            assertThat(followRepository.findById(follow.getId())).isNotPresent();
        }

        @Test
        void 등록되지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.deleteFollower(-1L, loginMember))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 나를_팔로우하지_않은_회원이라면_예외를_던진다() {
            Member loginMember = memberTestPersister.builder().save();
            Member followMember = memberTestPersister.builder().save();

            assertThatThrownBy(() -> followService.deleteFollower(followMember.getId(), loginMember))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("나를 팔로우 하지 않은 회원입니다.");
        }
    }
}
