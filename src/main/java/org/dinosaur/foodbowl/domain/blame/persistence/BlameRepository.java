package org.dinosaur.foodbowl.domain.blame.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.blame.domain.Blame;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.springframework.data.repository.Repository;

public interface BlameRepository extends Repository<Blame, Long> {

    Optional<Blame> findByMemberAndTargetIdAndBlameTarget(Member member, Long targetId, BlameTarget blameTarget);

    Blame save(Blame blame);
}
