package org.dinosaur.foodbowl.domain.follow.persistence.dto;

import com.querydsl.core.annotations.QueryProjection;

public record MemberFollowerCountDto(
        Long memberId,
        long followerCount
) {

    @QueryProjection
    public MemberFollowerCountDto {
    }
}
