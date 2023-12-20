package org.dinosaur.foodbowl.domain.follow.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

@SuppressWarnings("NonAsciiCharacters")
class FollowRepositoryTest extends PersistenceTest {

    @Autowired
    private FollowRepository followRepository;

    @Test
    void 팔로잉_목록을_페이징_조회한다() {
        Member follower = memberTestPersister.builder().save();
        Member followingA = memberTestPersister.builder().save();
        Member followingB = memberTestPersister.builder().save();

        Follow followA = followTestPersister.builder().following(followingA).follower(follower).save();
        Follow followB = followTestPersister.builder().following(followingB).follower(follower).save();

        Pageable pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());
        Slice<Follow> result = followRepository.findAllByFollower(follower, pageable);

        assertSoftly(softly -> {
            softly.assertThat(result.getContent().size()).isEqualTo(1);
            softly.assertThat(result.getContent().get(0)).isEqualTo(followB);
            softly.assertThat(result.isFirst()).isTrue();
            softly.assertThat(result.isLast()).isFalse();
            softly.assertThat(result.hasNext()).isTrue();
            softly.assertThat(result.getNumber()).isEqualTo(0);
            softly.assertThat(result.getSize()).isEqualTo(1);
        });
    }

    @Test
    void 팔로워_목록을_페이징_조회한다() {
        Member following = memberTestPersister.builder().save();
        Member followerA = memberTestPersister.builder().save();
        Member followerB = memberTestPersister.builder().save();

        Follow followA = followTestPersister.builder().following(following).follower(followerA).save();
        Follow followB = followTestPersister.builder().following(following).follower(followerB).save();

        Pageable pageable = PageRequest.of(0, 1, Sort.by("createdAt").descending());
        Slice<Follow> result = followRepository.findAllByFollowing(following, pageable);

        assertSoftly(softly -> {
            softly.assertThat(result.getContent().size()).isEqualTo(1);
            softly.assertThat(result.getContent().get(0)).isEqualTo(followB);
            softly.assertThat(result.isFirst()).isTrue();
            softly.assertThat(result.isLast()).isFalse();
            softly.assertThat(result.hasNext()).isTrue();
            softly.assertThat(result.getNumber()).isEqualTo(0);
            softly.assertThat(result.getSize()).isEqualTo(1);
        });
    }

    @Nested
    class 팔로우_연관_회원으로_조회 {

        private Member following;
        private Member follower;
        private Member other;

        @BeforeEach
        void setUp() {
            following = memberTestPersister.builder().save();
            follower = memberTestPersister.builder().save();
            other = memberTestPersister.builder().save();

            followTestPersister.builder()
                    .following(following)
                    .follower(follower)
                    .save();
        }

        @Test
        void 팔로잉_팔로우_회원이_모두_일치하면_팔로우를_반환한다() {
            Optional<Follow> result = followRepository.findByFollowingAndFollower(following, follower);

            assertThat(result).isPresent();
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
    }

    @Test
    void 팔로잉_수를_조회한다() {
        Member follower = memberTestPersister.builder().save();
        Member followingA = memberTestPersister.builder().save();
        Member followingB = memberTestPersister.builder().save();

        followTestPersister.builder().following(followingA).follower(follower).save();
        followTestPersister.builder().following(followingB).follower(follower).save();

        long result = followRepository.countByFollower(follower);

        assertThat(result).isEqualTo(2);
    }

    @Nested
    class 멤버의_팔로우_데이터_삭제_시 {

        @Test
        void 팔로잉_목록을_삭제한다() {
            Member member = memberTestPersister.builder().save();
            Follow followA = followTestPersister.builder().follower(member).save();
            Follow followB = followTestPersister.builder().follower(member).save();

            followRepository.deleteByMember(member);

            assertSoftly(softly -> {
                softly.assertThat(followRepository.findById(followA.getId())).isNotPresent();
                softly.assertThat(followRepository.findById(followB.getId())).isNotPresent();
            });
        }

        @Test
        void 팔로우_목록을_삭제한다() {
            Member member = memberTestPersister.builder().save();
            Follow followA = followTestPersister.builder().following(member).save();
            Follow followB = followTestPersister.builder().following(member).save();

            followRepository.deleteByMember(member);

            assertSoftly(softly -> {
                softly.assertThat(followRepository.findById(followA.getId())).isNotPresent();
                softly.assertThat(followRepository.findById(followB.getId())).isNotPresent();
            });
        }
    }
}
