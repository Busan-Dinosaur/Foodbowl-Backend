package org.dinosaur.foodbowl.domain.store.persistence;

import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.springframework.data.repository.Repository;

public interface StoreSchoolRepository extends Repository<StoreSchool, Long> {

    StoreSchool save(StoreSchool storeSchool);

    List<StoreSchool> findAllBySchool_Name_Name(String name);
}
