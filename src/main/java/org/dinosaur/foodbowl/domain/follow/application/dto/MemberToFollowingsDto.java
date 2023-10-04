package org.dinosaur.foodbowl.domain.follow.application.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.FollowAndFollowingDto;

public record MemberToFollowingsDto(
        Set<FollowAndFollowingDto> followAndFollowingDtos
) {

    public static MemberToFollowingsDto from(List<FollowAndFollowingDto> followAndFollowingDtos) {
        return new MemberToFollowingsDto(new HashSet<>(followAndFollowingDtos));
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        FollowAndFollowingDto followAndFollowingDto = new FollowAndFollowingDto(followerId, followingId);
        return followAndFollowingDtos.contains(followAndFollowingDto);
    }
}
