package org.dinosaur.foodbowl.domain.follow.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class FollowRepositoryTest extends RepositoryTest {

    @Autowired
    private FollowRepository followRepository;

    @Test
    @DisplayName("멤버의 팔로잉 목록을 삭제한다.")
    void deleteAllByFollower() {
        Member member = memberTestSupport.memberBuilder().build();
        followTestSupport.builder().follower(member).build();

        followRepository.deleteAllByFollower(member);

        assertThat(followRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("멤버의 팔로우 목록을 삭제한다.")
    void deleteAllByFollowing() {
        Member member = memberTestSupport.memberBuilder().build();
        followTestSupport.builder().following(member).build();

        followRepository.deleteAllByFollowing(member);

        assertThat(followRepository.findAll()).isEmpty();
    }
}
