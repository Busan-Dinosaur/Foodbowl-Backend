package org.dinosaur.foodbowl.domain.photo.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ThumbnailTest {

    @Test
    void 썸네일을_생성한다() {
        Thumbnail thumbnail = Thumbnail.builder()
                .path("http://foodbowl.com/static/images/image.png")
                .width(100)
                .height(100)
                .build();

        assertThat(thumbnail.getPath()).isEqualTo("http://foodbowl.com/static/images/image.png");
    }
}
