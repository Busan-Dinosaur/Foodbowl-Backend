package org.dinosaur.foodbowl.testsupport;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.entity.Blame;
import org.dinosaur.foodbowl.domain.blame.entity.Blame.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.repository.BlameRepository;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlameTestSupport {

    private final MemberTestSupport memberTestSupport;
    private final BlameRepository blameRepository;

    public BlameBuilder builder() {
        return new BlameBuilder();
    }

    public final class BlameBuilder {

        private Member member;
        private Long targetId;
        private BlameTarget blameTarget;

        public BlameBuilder member(Member member) {
            this.member = member;
            return this;
        }

        public BlameBuilder targetId(Long targetId) {
            this.targetId = targetId;
            return this;
        }

        public BlameBuilder blameTarget(BlameTarget blameTarget) {
            this.blameTarget = blameTarget;
            return this;
        }

        public Blame build() {
            return blameRepository.save(
                    Blame.builder()
                            .member(member == null ? memberTestSupport.memberBuilder().build() : member)
                            .targetId(targetId == null ? 1L : targetId)
                            .blameTarget(blameTarget == null ? BlameTarget.POST : blameTarget)
                            .build()
            );
        }
    }
}
