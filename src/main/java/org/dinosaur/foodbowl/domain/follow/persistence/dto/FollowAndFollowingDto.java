package org.dinosaur.foodbowl.domain.follow.persistence.dto;

import java.util.Objects;

public record FollowAndFollowingDto(
        Long followerId,
        Long followingId
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FollowAndFollowingDto that = (FollowAndFollowingDto) o;
        return Objects.equals(followerId, that.followerId) && Objects.equals(followingId,
                that.followingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followerId, followingId);
    }
}
