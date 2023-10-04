package org.dinosaur.foodbowl.domain.follow.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.FollowAndFollowingDto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberToFollowingsDtoTest {

    @Test
    void 팔로잉_하는_상황이면_true를_반환한다() {
        List<FollowAndFollowingDto> followAndFollowingDtos = List.of(
                new FollowAndFollowingDto(1L, 2L)
        );
        MemberToFollowingsDto memberToFollowingsDto = new MemberToFollowingsDto(new HashSet<>(followAndFollowingDtos));

        assertThat(memberToFollowingsDto.isFollowing(1L, 2L)).isTrue();
    }

    @Test
    void 팔로잉_하지_않는_상황이면_false를_반환한다() {
        List<FollowAndFollowingDto> followAndFollowingDtos = List.of(
                new FollowAndFollowingDto(1L, 2L)
        );
        MemberToFollowingsDto memberToFollowingsDto = new MemberToFollowingsDto(new HashSet<>(followAndFollowingDtos));

        assertThat(memberToFollowingsDto.isFollowing(1L, 3L)).isFalse();
    }
}
