package org.dinosaur.foodbowl.domain.photo.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class PhotoServiceTest extends IntegrationTest {

    @Autowired
    private PhotoService photoService;

    @Test
    void 사진을_저장한다() {
        Photo photo1 = Photo.builder()
                .path("https://justdoeat.shop/bucket/bbq/image1.jpg")
                .build();
        Photo photo2 = Photo.builder()
                .path("https://justdoeat.shop/bucket/bbq/image2.jpg")
                .build();
        List<Photo> photos = List.of(photo1, photo2);

        assertDoesNotThrow(() -> photoService.save(photos));
    }
}
