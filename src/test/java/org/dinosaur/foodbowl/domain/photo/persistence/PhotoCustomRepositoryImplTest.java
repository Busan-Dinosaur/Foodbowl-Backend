package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class PhotoCustomRepositoryImplTest extends PersistenceTest {

    @Autowired
    private PhotoCustomRepositoryImpl photoCustomRepository;

    @Test
    void 사진을_삭제한다() {
        Photo photo = photoTestPersister.builder().save();

        long deleteCount = photoCustomRepository.deleteByPhoto(photo);

        assertThat(deleteCount).isEqualTo(1);
    }

    @Test
    void 사진을_모두_삭제한다() {
        Photo photoA = photoTestPersister.builder().save();
        Photo photoB = photoTestPersister.builder().save();

        long deleteCount = photoCustomRepository.deleteAllByPhotos(List.of(photoA, photoB));

        assertThat(deleteCount).isEqualTo(2);
    }
}
