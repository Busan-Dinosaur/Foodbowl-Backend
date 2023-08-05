package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.store.dto.response.CategoryResponses;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreServiceTest extends IntegrationTest {

    @Autowired
    private StoreService storeService;

    @Test
    void 가게_카테고리_목록을_조회한다() {
        CategoryResponses result = storeService.getCategories();

        assertThat(result.categories()).hasSize(11);
    }
}
