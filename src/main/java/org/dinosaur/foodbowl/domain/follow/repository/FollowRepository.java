package org.dinosaur.foodbowl.domain.follow.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.follow.entity.Follow;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.data.repository.Repository;

public interface FollowRepository extends Repository<Follow, Long> {

    List<Follow> findAll();

    Follow save(Follow follow);

    void deleteAllByFollowing(Member following);

    void deleteAllByFollower(Member follower);
}
