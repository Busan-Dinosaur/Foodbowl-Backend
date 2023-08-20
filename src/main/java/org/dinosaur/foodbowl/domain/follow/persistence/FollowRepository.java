package org.dinosaur.foodbowl.domain.follow.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.data.repository.Repository;

public interface FollowRepository extends Repository<Follow, Long> {

    Follow save(Follow follow);

    Optional<Follow> findByFollowingAndFollower(Member following, Member follower);
}