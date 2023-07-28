package org.dinosaur.foodbowl.domain.blame.persistence;

import org.dinosaur.foodbowl.domain.blame.domain.Blame;
import org.springframework.data.repository.Repository;

public interface BlameRepository extends Repository<Blame, Long> {
}
