package org.dinosaur.foodbowl.domain.follow.persistence.dto;

import com.querydsl.core.annotations.QueryProjection;
import java.util.Objects;

public record FollowerAndFollowingDto(
        Long followerId,
        Long followingId
) {

    @QueryProjection
    public FollowerAndFollowingDto {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FollowerAndFollowingDto that = (FollowerAndFollowingDto) o;
        return Objects.equals(followerId, that.followerId)
                && Objects.equals(followingId, that.followingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followingId);
    }
}
