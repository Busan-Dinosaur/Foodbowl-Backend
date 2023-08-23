package org.dinosaur.foodbowl.domain.store.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreSchoolServiceTest extends IntegrationTest {

    @Autowired
    private StoreSchoolService storeSchoolService;

    @Test
    void 가게와_학교_매핑_정보를_저장한다() {
        Store store = storeTestPersister.builder().save();
        School school = schoolTestPersister.builder().save();

        assertDoesNotThrow(() -> storeSchoolService.save(store, school));
    }
}
