package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ThumbnailCustomRepositoryImplTest extends PersistenceTest {

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Test
    void 썸네일을_삭제한다() {
        Thumbnail thumbnail = Thumbnail.builder()
                .path("http://justdoeat.shop/store/1/image.jpg")
                .build();
        Thumbnail saveThumbnail = thumbnailRepository.save(thumbnail);

        long deleteCount = thumbnailRepository.deleteByThumbnail(saveThumbnail);

        assertThat(deleteCount).isEqualTo(1);
    }
}
