package org.dinosaur.foodbowl.domain.follow.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface FollowRepository extends Repository<Follow, Long> {

    Optional<Follow> findById(Long id);

    @Query("select f from Follow f"
            + " left join fetch f.follower.memberThumbnail.thumbnail"
            + " where f.follower = :follower"
    )
    Slice<Follow> findAllByFollower(Member follower, Pageable pageable);

    @Query("select f from Follow f"
            + " left join fetch f.follower.memberThumbnail.thumbnail"
            + " where f.following = :following"
    )
    Slice<Follow> findAllByFollowing(Member following, Pageable pageable);

    Optional<Follow> findByFollowingAndFollower(Member following, Member follower);

    Follow save(Follow follow);

    void delete(Follow follow);
}
