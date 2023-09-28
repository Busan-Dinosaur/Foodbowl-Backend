package org.dinosaur.foodbowl.domain.photo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.photo.domain.QPhoto.photo;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class PhotoServiceTest extends IntegrationTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private PhotoService photoService;

    @Test
    void 사진을_저장한다() {
        MultipartFile multipartFile = FileTestUtils.generateMultiPartFile("image");

        Photo photo = photoService.save(multipartFile, "workingDirectory");

        assertThat(photo.getId()).isNotNull();
        FileTestUtils.cleanUp();
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
        FileTestUtils.cleanUp();
    }
}
