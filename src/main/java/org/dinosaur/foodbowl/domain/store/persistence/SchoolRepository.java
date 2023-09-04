package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.springframework.data.repository.Repository;

public interface SchoolRepository extends Repository<School, Long> {

    Optional<School> findByName(SchoolName schoolName);

    List<School> findAllByOrderByName();

    School save(School school);
}
