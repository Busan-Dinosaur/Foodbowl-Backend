package org.dinosaur.foodbowl.domain.follow.persistence.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FollowAndFollowingDtoTest {

    @Test
    void 팔로우_ID와_팔로잉_ID가_동일하면_같은_객체이다() {
        FollowAndFollowingDto followAndFollowingDtoA = new FollowAndFollowingDto(1L, 2L);
        FollowAndFollowingDto followAndFollowingDtoB = new FollowAndFollowingDto(1L, 2L);

        assertThat(followAndFollowingDtoA).isEqualTo(followAndFollowingDtoB);
    }

    @Test
    void 팔로우_ID와_팔로잉_ID가_다르면_다른_객체이다() {
        FollowAndFollowingDto followAndFollowingDtoA = new FollowAndFollowingDto(1L, 2L);
        FollowAndFollowingDto followAndFollowingDtoB = new FollowAndFollowingDto(2L, 1L);

        assertThat(followAndFollowingDtoA).isNotEqualTo(followAndFollowingDtoB);
    }
}
