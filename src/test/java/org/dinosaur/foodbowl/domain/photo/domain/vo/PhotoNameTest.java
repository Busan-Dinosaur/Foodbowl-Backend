package org.dinosaur.foodbowl.domain.photo.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PhotoNameTest {

    @Test
    void 파일_확장자를_포함한_이름을_반환한다() {
        String fileName = "image.jpg";

        String photoName = PhotoName.of(fileName);

        assertThat(photoName).endsWith(".jpg");
    }
}
