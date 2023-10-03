package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;

@RequiredArgsConstructor
@Persister
public class StoreSchoolTestPersister {

    private final StoreSchoolRepository storeSchoolRepository;
    private final StoreTestPersister storeTestPersister;
    private final SchoolTestPersister schoolTestPersister;

    public StoreSchoolBuilder builder() {
        return new StoreSchoolBuilder();
    }

    public final class StoreSchoolBuilder {

        private Store store;
        private School school;

        public StoreSchoolBuilder store(Store store) {
            this.store = store;
            return this;
        }

        public StoreSchoolBuilder school(School school) {
            this.school = school;
            return this;
        }

        public StoreSchool save() {
            StoreSchool storeSchool = StoreSchool.builder()
                    .store(store == null ? storeTestPersister.builder().save() : store)
                    .school(school == null ? schoolTestPersister.builder().save() : school)
                    .build();
            return storeSchoolRepository.save(storeSchool);
        }
    }
}
