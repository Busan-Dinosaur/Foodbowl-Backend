package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.domain.Blame;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.persistence.BlameRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;

@RequiredArgsConstructor
@Persister
public class BlameTestPersister {

    private final BlameRepository blameRepository;
    private final MemberTestPersister memberTestPersister;

    public BlameBuilder builder() {
        return new BlameBuilder();
    }

    public final class BlameBuilder {

        private Member member;
        private Long targetId;
        private BlameTarget blameTarget;
        private String description;

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

        public BlameBuilder description(String description) {
            this.description = description;
            return this;
        }

        public Blame save() {
            Blame blame = Blame.builder()
                    .member(member == null ? memberTestPersister.memberBuilder().save() : member)
                    .targetId(targetId == null ? 1L : targetId)
                    .blameTarget(blameTarget == null ? BlameTarget.MEMBER : blameTarget)
                    .description(description == null ? "invalid" : description)
                    .build();
            return blameRepository.save(blame);
        }
    }
}
