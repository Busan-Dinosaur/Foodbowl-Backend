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
        List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(3);

        List<Photo> photos = photoService.save(multipartFiles, "workingDirectory");

        assertThat(photos).hasSize(3);
        FileTestUtils.cleanUp();
    }

    @Test
    void 사진을_삭제한다() {
        List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(3);
        List<Photo> photos = photoService.save(multipartFiles, "workingDirectory");

        photoService.delete(photos);

        List<Photo> selectPhotos = jpaQueryFactory.selectFrom(photo)
                .where(photo.in(photos))
                .fetch();
        assertThat(selectPhotos).isEmpty();
        FileTestUtils.cleanUp();
    }
}
