package org.dinosaur.foodbowl.domain.follow.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class FollowRepositoryTest extends PersistenceTest {

    @Autowired
    private FollowRepository followRepository;

    @Nested
    class 팔로우_연관_회원으로_조회 {

        private Member following;
        private Member follower;
        private Member other;

        @BeforeEach
        void setUp() {
            following = memberTestPersister.memberBuilder().save();
            follower = memberTestPersister.memberBuilder().save();
            other = memberTestPersister.memberBuilder().save();

            followTestPersister.builder()
                    .following(following)
                    .follower(follower)
                    .save();
        }

        @Test
        void 팔로잉_회원이_일치하지_않으면_빈값을_반환한다() {
            Optional<Follow> result = followRepository.findByFollowingAndFollower(other, follower);

            assertThat(result).isEmpty();
        }

        @Test
        void 팔로우_회원이_일치하지_않으면_빈값을_반환한다() {
            Optional<Follow> result = followRepository.findByFollowingAndFollower(following, other);

            assertThat(result).isEmpty();
        }

        @Test
        void 팔로잉_팔로우_회원이_모두_일치하지_않으면_빈값을_반환한다() {
            Optional<Follow> result = followRepository.findByFollowingAndFollower(other, other);

            assertThat(result).isEmpty();
        }

        @Test
        void 팔로잉_팔로우_회원이_모두_일치하면_팔로우를_반환한다() {
            Optional<Follow> result = followRepository.findByFollowingAndFollower(following, follower);

            assertThat(result).isPresent();
        }
    }
}
