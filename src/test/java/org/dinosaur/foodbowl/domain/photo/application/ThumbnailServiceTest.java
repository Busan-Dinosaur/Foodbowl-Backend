package org.dinosaur.foodbowl.domain.photo.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.dinosaur.foodbowl.domain.photo.domain.QThumbnail.thumbnail;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.File;
import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class ThumbnailServiceTest extends IntegrationTest {

    @Autowired
    private ThumbnailService thumbnailService;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @AfterEach
    void tearDown() {
        FileTestUtils.cleanUp();
    }

    @Test
    void 썸네일을_저장한다() {
        MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();

        Thumbnail thumbnail = thumbnailService.save(multipartFile);

        assertSoftly(softly -> {
            softly.assertThat(thumbnail.getId()).isNotNull();
            softly.assertThat(new File(thumbnail.getPath())).exists();
        });
    }

    @Test
    void 썸네일을_삭제한다() {
        MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();
        Thumbnail saveThumbnail = thumbnailService.save(multipartFile);

        thumbnailService.delete(saveThumbnail);

        List<Thumbnail> selectThumbnails = jpaQueryFactory.selectFrom(thumbnail)
                .where(thumbnail.id.eq(saveThumbnail.getId()))
                .fetch();
        assertSoftly(softly -> {
            softly.assertThat(selectThumbnails).isEmpty();
            softly.assertThat(new File(saveThumbnail.getPath())).doesNotExist();
        });
    }
}
