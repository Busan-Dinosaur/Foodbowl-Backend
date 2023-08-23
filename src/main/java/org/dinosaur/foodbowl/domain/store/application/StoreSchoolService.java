package org.dinosaur.foodbowl.domain.store.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreSchoolService {

    private final StoreSchoolRepository storeSchoolRepository;

    @Transactional
    public void save(Store store, School school) {
        StoreSchool storeSchool = StoreSchool.builder()
                .store(store)
                .school(school)
                .build();
        storeSchoolRepository.save(storeSchool);
    }
}
