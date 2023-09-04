package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.springframework.data.repository.Repository;

public interface StoreSchoolRepository extends Repository<StoreSchool, Long> {

    List<StoreSchool> findAllBySchool_Name(SchoolName name);

    StoreSchool save(StoreSchool storeSchool);
}
