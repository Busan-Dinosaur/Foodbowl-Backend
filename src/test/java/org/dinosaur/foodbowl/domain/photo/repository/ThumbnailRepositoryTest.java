package org.dinosaur.foodbowl.domain.photo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.dinosaur.foodbowl.RepositoryTest;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ThumbnailRepositoryTest extends RepositoryTest {

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Test
    @DisplayName("썸네일을 삭제한다.")
    void delete() {
        Thumbnail thumbnail = thumbnailTestSupport.builder().build();

        thumbnailRepository.delete(thumbnail);

        assertThat(thumbnailRepository.findAll()).isEmpty();
    }
}
