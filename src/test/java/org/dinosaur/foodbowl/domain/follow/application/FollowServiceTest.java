package org.dinosaur.foodbowl.domain.follow.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
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
}
