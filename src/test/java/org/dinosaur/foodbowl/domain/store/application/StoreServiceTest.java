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
    void 카테고리_목록을_조회한다() {
        CategoryResponses response = storeService.getCategories();

        assertThat(response.categories()).hasSize(11);
    }
}
