package org.dinosaur.foodbowl.domain.photo.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.photo.domain.QThumbnail.thumbnail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.test.PersistenceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class ThumbnailRepositoryTest extends PersistenceTest {

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Test
    void 썸네일을_저장한다() {
        Thumbnail thumbnail = Thumbnail.builder()
                .path("http://justdoeat.shop/store/1/image.png")
                .build();

        Thumbnail saveThumbnail = thumbnailRepository.save(thumbnail);

        assertThat(saveThumbnail.getId()).isNotNull();
    }

    @Test
    void 썸네일을_삭제한다() {
        Thumbnail saveThumbnail = thumbnailTestPersister.builder().save();

        thumbnailRepository.delete(saveThumbnail);

        Thumbnail selectThumbnail = jpaQueryFactory.selectFrom(thumbnail)
                .where(thumbnail.id.eq(saveThumbnail.getId()))
                .fetchOne();
        assertThat(selectThumbnail).isNull();
    }
}
