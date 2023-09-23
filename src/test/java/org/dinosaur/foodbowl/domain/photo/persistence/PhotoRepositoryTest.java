package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class PhotoRepositoryTest extends PersistenceTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    void 사진을_저장한다() {
        Photo photo = Photo.builder()
                .path("http://justdoeat.shop/store/1/image.jpg")
                .build();

        Photo savePhoto = photoRepository.save(photo);

        assertThat(savePhoto.getId()).isNotNull();
    }

    @Test
    void 사진을_삭제한다() {
        Photo photo = Photo.builder()
                .path("http://justdoeat.shop/store/1/image.jpg")
                .build();
        Photo savePhoto = photoRepository.save(photo);

        long deleteCount = photoRepository.deleteByPhoto(savePhoto);

        assertThat(deleteCount).isEqualTo(1);
    }
}
