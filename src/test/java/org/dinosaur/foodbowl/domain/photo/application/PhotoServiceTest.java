package org.dinosaur.foodbowl.domain.photo.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;
import org.dinosaur.foodbowl.file.FileTestUtils;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class PhotoServiceTest extends IntegrationTest {

    @Autowired
    private PhotoService photoService;

    @Test
    void 사진을_저장한다() {
        List<MultipartFile> multipartFiles = FileTestUtils.generateMultipartFiles(3);

        assertDoesNotThrow(() -> photoService.save(multipartFiles, "parentDirectory"));

        FileTestUtils.cleanUp();
    }
}
