package org.dinosaur.foodbowl.domain.photo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.List;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class PhotoLocalManagerTest extends IntegrationTest {

    @Autowired
    private PhotoLocalManager photoLocalUploader;

    private static final byte[] image = FileTestUtils.generateMockImage();

    @Nested
    class 이미지_파일_저장_시 {

        @Test
        void 디렉토리에_저장한다() {
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();
            List<MultipartFile> multipartFiles = List.of(multipartFile);

            List<String> filePaths = photoLocalUploader.upload(multipartFiles, "test");

            String imagePath = filePaths.get(0);
            File file = new File(imagePath);
            assertThat(file.exists()).isTrue();
            FileTestUtils.cleanUp();
        }

        @ParameterizedTest
        @ValueSource(strings = {"foodBowl.zip", "foodBowl.pdf", "foodBowl.docx", "foodBowl.pptx"})
        void 파일_이름이_이미지_형식이_아니면_예외가_발생한다(String originalFilename) {
            MockMultipartFile multipartFile =
                    new MockMultipartFile("bucket", originalFilename, MediaType.IMAGE_JPEG_VALUE, image);
            List<MultipartFile> multipartFiles = List.of(multipartFile);

            assertThatThrownBy(() -> photoLocalUploader.upload(multipartFiles, "test"))
                    .isInstanceOf(FileException.class)
                    .hasMessage("이미지 파일만 업로드 가능합니다.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void 파일_이름이_없으면_예외가_발생한다(String originalFilename) {
            MockMultipartFile multipartFile =
                    new MockMultipartFile("bucket", originalFilename, MediaType.IMAGE_JPEG_VALUE, image);
            List<MultipartFile> multipartFiles = List.of(multipartFile);

            assertThatThrownBy(() -> photoLocalUploader.upload(multipartFiles, "test"))
                    .isInstanceOf(FileException.class)
                    .hasMessage("파일 이름은 공백이 될 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"foodBowlZip", "helloWorld", "javaSpring"})
        void 확장자가_없으면_예외가_발생한다(String originalFilename) {
            MockMultipartFile multipartFile =
                    new MockMultipartFile("bucket", originalFilename, MediaType.IMAGE_JPEG_VALUE, image);
            List<MultipartFile> multipartFiles = List.of(multipartFile);

            assertThatThrownBy(() -> photoLocalUploader.upload(multipartFiles, "test"))
                    .isInstanceOf(FileException.class)
                    .hasMessage("파일에 확장자가 존재하지 않습니다.");
        }
    }

    @Nested
    class 이미지_파일_삭제_시 {

        @Test
        void 정상적으로_삭제한다() {
            MultipartFile multipartFile = FileTestUtils.generateMultiPartFile();
            List<MultipartFile> multipartFiles = List.of(multipartFile);
            List<String> filePaths = photoLocalUploader.upload(multipartFiles, "test");

            photoLocalUploader.delete(filePaths);

            String imagePath = filePaths.get(0);
            File file = new File(imagePath);
            assertThat(file.exists()).isFalse();
            FileTestUtils.cleanUp();
        }

        @Test
        void 파일_이름에_URL_정보가_없으면_예외가_발생한다() {
            String fullPath = "https://image.url";

            assertThatThrownBy(() -> photoLocalUploader.delete(List.of(fullPath)))
                    .isInstanceOf(FileException.class)
                    .hasMessage("파일의 URL 정보를 찾을 수 없습니다.");
        }
    }
}
