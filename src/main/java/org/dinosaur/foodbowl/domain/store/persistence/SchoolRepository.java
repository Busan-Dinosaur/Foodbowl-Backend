package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.springframework.data.repository.Repository;

public interface SchoolRepository extends Repository<School, Long> {

    Optional<School> findByName(String name);

    School save(School school);
}
