package org.dinosaur.foodbowl.domain.follow.persistence.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FollowerAndFollowingDtoTest {

    @Test
    void 팔로우_ID와_팔로잉_ID가_동일하면_같은_객체이다() {
        FollowerAndFollowingDto followerAndFollowingDtoA = new FollowerAndFollowingDto(1L, 2L);
        FollowerAndFollowingDto followerAndFollowingDtoB = new FollowerAndFollowingDto(1L, 2L);

        assertThat(followerAndFollowingDtoA).isEqualTo(followerAndFollowingDtoB);
    }

    @Test
    void 팔로우_ID와_팔로잉_ID가_다르면_다른_객체이다() {
        FollowerAndFollowingDto followerAndFollowingDtoA = new FollowerAndFollowingDto(1L, 2L);
        FollowerAndFollowingDto followerAndFollowingDtoB = new FollowerAndFollowingDto(2L, 1L);

        assertThat(followerAndFollowingDtoA).isNotEqualTo(followerAndFollowingDtoB);
    }
}
