package org.dinosaur.foodbowl.domain.photo;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class PhotoUtilsTest {
    private final List<String> trash = new LinkedList<>();

    private final PhotoUtils photoUtils = new PhotoUtils();
    private MockMultipartFile testImage;

    @BeforeEach
    void init() throws IOException {
        final String fileName = "testImage";
        final String contentType = "jpg";
        final String savedPath = "src/test/resources/test_image/testImage.jpg";

        FileInputStream fileInputStream = new FileInputStream(savedPath);

        testImage = new MockMultipartFile(
                "images",
                fileName + "." + contentType,
                contentType,
                fileInputStream
        );
    }

    @AfterEach
    void deleteFiles() {
        photoUtils.deleteImageFiles(trash);
    }

    @Test
    @DisplayName("이미지 파일을 저장하면 \"지정 경로 + UUID + 확장자\"를 반환한다")
    void storeImageFile() throws IOException {
        String path = photoUtils.storeImageFile(testImage);
        trash.add(path);

        assertThat(path).isNotNull();
    }

    @Test
    @DisplayName("이미지 파일 여러개를 저장하면 \"지정 경로 + UUID + 확장자\"를 가진 List를 반환한다")
    void storeImageFiles() {
        int imageFileCount = 2;
        List<MultipartFile> testImageFiles = new ArrayList<>(imageFileCount);
        for (int i = 0; i < imageFileCount; i++) {
            testImageFiles.add(testImage);
        }

        List<String> paths = photoUtils.storeImageFiles(testImageFiles);

        trash.addAll(paths);
        assertThat(paths.size()).isEqualTo(imageFileCount);
    }
}