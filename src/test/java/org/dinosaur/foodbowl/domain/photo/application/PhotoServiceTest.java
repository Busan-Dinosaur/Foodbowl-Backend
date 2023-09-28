package org.dinosaur.foodbowl.domain.photo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.dinosaur.foodbowl.domain.photo.domain.QPhoto.photo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.io.File;
import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class PhotoServiceTest extends IntegrationTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        FileTestUtils.cleanUp();
    }

    @Test
    void 사진을_저장한다() {
        MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");

        Photo photo = photoService.save(multipartFile, "workingDirectory");

        assertSoftly(softly -> {
            softly.assertThat(photo.getId()).isNotNull();
            softly.assertThat(new File(photo.getPath())).exists();
        });
    }

    @Test
    void 사진을_모두_저장한다() {
        MultipartFile multipartFileA = FileTestUtils.generateMultiPartFile("image");
        MultipartFile multipartFileB = FileTestUtils.generateMultiPartFile("image");

        List<Photo> photos = photoService.saveAll(List.of(multipartFileA, multipartFileB), "workingDirectory");

        assertSoftly(softly -> {
            softly.assertThat(photos).hasSize(2);
            softly.assertThat(photos.get(0).getId()).isNotNull();
            softly.assertThat(new File(photos.get(0).getPath())).exists();
            softly.assertThat(photos.get(1).getId()).isNotNull();
            softly.assertThat(new File(photos.get(1).getPath())).exists();
        });
    }

    @Test
    void 사진을_삭제한다() {
        MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");
        Photo savePhoto = photoService.save(multipartFile, "workingDirectory");

        photoService.delete(savePhoto);

        List<Photo> selectPhotos = jpaQueryFactory.selectFrom(photo)
                .where(photo.id.eq(savePhoto.getId()))
                .fetch();
        assertThat(selectPhotos).isEmpty();
    }

    @Test
    void 사진을_모두_삭제한다() {
        MultipartFile multipartFileA = FileTestUtils.generateMultiPartFile("image");
        MultipartFile multipartFileB = FileTestUtils.generateMultiPartFile("image");
        List<Photo> photos = photoService.saveAll(List.of(multipartFileA, multipartFileB), "workingDirectory");

        photoService.deleteAll(photos);

        List<Photo> selectPhotos = jpaQueryFactory.selectFrom(photo)
                .where(photo.in(photos))
                .fetch();
        assertSoftly(softly -> {
            softly.assertThat(selectPhotos).isEmpty();
            softly.assertThat(new File(photos.get(0).getPath())).doesNotExist();
            softly.assertThat(new File(photos.get(1).getPath())).doesNotExist();
        });
    }
}
