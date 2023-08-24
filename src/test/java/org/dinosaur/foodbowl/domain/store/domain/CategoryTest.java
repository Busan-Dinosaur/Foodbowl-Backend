package org.dinosaur.foodbowl.domain.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CategoryTest {

    @Test
    void 카테고리를_생성한다() {
        Category category = Category.builder()
                .id(1L)
                .categoryType(CategoryType.카페)
                .build();

        assertThat(category.getName()).isEqualTo("카페");
    }
}
