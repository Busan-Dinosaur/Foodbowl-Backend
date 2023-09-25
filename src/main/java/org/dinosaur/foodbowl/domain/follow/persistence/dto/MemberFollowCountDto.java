package org.dinosaur.foodbowl.domain.follow.persistence.dto;

import com.querydsl.core.annotations.QueryProjection;

public record MemberFollowCountDto(
        Long memberId,
        long followCount
) {

    @QueryProjection
    public MemberFollowCountDto {
    }
}
