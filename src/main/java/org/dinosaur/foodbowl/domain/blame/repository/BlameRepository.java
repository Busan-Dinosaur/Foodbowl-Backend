package org.dinosaur.foodbowl.domain.blame.repository;

import java.util.List;
import org.dinosaur.foodbowl.domain.blame.entity.Blame;
import org.dinosaur.foodbowl.domain.blame.entity.Blame.BlameTarget;
import org.springframework.data.repository.Repository;

public interface BlameRepository extends Repository<Blame, Long> {

    Blame save(Blame blame);

    List<Blame> findAll();

    void deleteAllByTargetIdAndBlameTarget(Long targetId, BlameTarget blameTarget);
}
