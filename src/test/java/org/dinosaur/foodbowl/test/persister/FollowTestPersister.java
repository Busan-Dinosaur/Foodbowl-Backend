package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.domain.Follow;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@RequiredArgsConstructor
@Persister
public class FollowTestPersister {

    private final FollowRepository followRepository;
    private final MemberTestPersister memberTestPersister;

    public FollowBuilder builder() {
        return new FollowBuilder();
    }

    public final class FollowBuilder {

        private Member following;
        private Member follower;

        public FollowBuilder following(Member following) {
            this.following = following;
            return this;
        }

        public FollowBuilder follower(Member follower) {
            this.follower = follower;
            return this;
        }

        public Follow save() {
            Follow follow = Follow.builder()
                    .following(following == null ? memberTestPersister.memberBuilder().save() : following)
                    .follower(follower == null ? memberTestPersister.memberBuilder().save() : follower)
                    .build();
            return followRepository.save(follow);
        }
    }
}
