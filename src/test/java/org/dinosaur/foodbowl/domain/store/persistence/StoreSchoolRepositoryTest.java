package org.dinosaur.foodbowl.domain.store.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreSchoolRepositoryTest extends PersistenceTest {

    @Autowired
    private StoreSchoolRepository storeSchoolRepository;

    @Test
    void 학교와_관련된_가게를_가져온다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        StoreSchool storeSchool = StoreSchool.builder()
                .store(store)
                .school(school)
                .build();
        storeSchoolRepository.save(storeSchool);

        List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchool_Name(new SchoolName(school.getName()));
        List<Store> stores = storeSchools.stream()
                .map(StoreSchool::getStore)
                .toList();

        assertThat(stores).contains(store);
    }

    @Test
    void 가게와_학교의_관계를_저장한다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();
        StoreSchool storeSchool = StoreSchool.builder()
                .store(store)
                .school(school)
                .build();

        StoreSchool saveStoreSchool = storeSchoolRepository.save(storeSchool);

        assertSoftly(softly -> {
            softly.assertThat(saveStoreSchool.getId()).isNotNull();
            softly.assertThat(saveStoreSchool.getStore()).isEqualTo(store);
            softly.assertThat(saveStoreSchool.getSchool()).isEqualTo(school);
        });
    }
}
