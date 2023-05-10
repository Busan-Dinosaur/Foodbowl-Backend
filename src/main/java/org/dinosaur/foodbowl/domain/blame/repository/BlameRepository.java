package org.dinosaur.foodbowl.domain.blame.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.blame.entity.Blame;
import org.dinosaur.foodbowl.domain.blame.entity.Blame.BlameTarget;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.springframework.data.repository.Repository;

public interface BlameRepository extends Repository<Blame, Long> {

    List<Blame> findAll();

    Blame save(Blame blame);

    void deleteAllByMember(Member member);

    void deleteAllByTargetIdAndBlameTarget(Long targetId, BlameTarget blameTarget);
}
