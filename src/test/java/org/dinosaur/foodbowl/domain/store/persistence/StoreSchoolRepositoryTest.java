package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.dinosaur.foodbowl.PersistenceTest;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreSchoolRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreSchoolRepository storeSchoolRepository;

    @Test
    void 가게와_학교의_관계를_저장한다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        StoreSchool storeSchool = StoreSchool.builder()
                .store(store)
                .school(school)
                .build();

        StoreSchool saveStoreSchool = storeSchoolRepository.save(storeSchool);

        assertAll(
                () -> assertThat(saveStoreSchool.getId()).isNotNull(),
                () -> assertThat(saveStoreSchool.getStore()).isEqualTo(store),
                () -> assertThat(saveStoreSchool.getSchool()).isEqualTo(school)
        );
    }

    @Test
    void 학교와_관련된_가게를_가져온다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        StoreSchool storeSchool = StoreSchool.builder()
                .store(store)
                .school(school)
                .build();
        storeSchoolRepository.save(storeSchool);

        List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchoolName(school.getName().getName());
        List<Store> stores = storeSchools.stream()
                .map(StoreSchool::getStore)
                .toList();

        assertThat(stores).contains(store);
    }
}
