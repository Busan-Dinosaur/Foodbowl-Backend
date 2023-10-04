package org.dinosaur.foodbowl.domain.follow.application.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.FollowerAndFollowingDto;

public record MemberToFollowingsDto(
        Set<FollowerAndFollowingDto> followerAndFollowingDtos
) {

    public static MemberToFollowingsDto from(List<FollowerAndFollowingDto> followerAndFollowingDtos) {
        return new MemberToFollowingsDto(new HashSet<>(followerAndFollowingDtos));
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        FollowerAndFollowingDto followerAndFollowingDto = new FollowerAndFollowingDto(followerId, followingId);
        return followerAndFollowingDtos.contains(followerAndFollowingDto);
    }
}
