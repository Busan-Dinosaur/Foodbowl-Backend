package org.dinosaur.foodbowl.testsupport;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.entity.Follow;
import org.dinosaur.foodbowl.domain.follow.repository.FollowRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FollowTestSupport {

    private final MemberTestSupport memberTestSupport;
    private final FollowRepository followRepository;

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

        public Follow build() {
            return followRepository.save(
                    Follow.builder()
                            .following(following == null ? memberTestSupport.memberBuilder().build() : following)
                            .follower(follower == null ? memberTestSupport.memberBuilder().build() : follower)
                            .build()
            );
        }
    }
}
