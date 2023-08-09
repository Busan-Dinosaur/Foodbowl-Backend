package org.dinosaur.foodbowl.domain.photo.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.List;
import org.dinosaur.foodbowl.IntegrationTest;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
class FileSystemPhotoUtilsTest extends IntegrationTest {

    @Autowired
    private FileSystemPhotoUtils fileSystemPhotoService;

    @Test
    void 파일을_디렉토리에_저장한다() {
        MockMultipartFile multipartFile = new MockMultipartFile("bucket", "foodBowl.jpg", MediaType.TEXT_PLAIN_VALUE,
                "Hello Images".getBytes());
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        List<String> filePaths = fileSystemPhotoService.upload(multipartFiles, "test");

        assertThat(filePaths).isNotEmpty();
        deleteImageFile(filePaths);
    }

    @ParameterizedTest
    @ValueSource(strings = {"foodBowl.zip", "foodBowl.pdf", "foodBowl.docx", "foodBowl.pptx"})
    void 이미지_파일이_아니면_예외가_발생한다(String originalFilename) {
        MockMultipartFile multipartFile = new MockMultipartFile("bucket", originalFilename, MediaType.TEXT_PLAIN_VALUE,
                "Hello Images".getBytes());
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        assertThatThrownBy(() -> fileSystemPhotoService.upload(multipartFiles, "test"))
                .isInstanceOf(FileException.class)
                .hasMessage("이미지 파일만 업로드 가능합니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void 파일_이름이_없으면_예외가_발생한다(String originalFilename) {
        MockMultipartFile multipartFile = new MockMultipartFile("bucket", originalFilename, MediaType.TEXT_PLAIN_VALUE,
                "Hello Images".getBytes());
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        assertThatThrownBy(() -> fileSystemPhotoService.upload(multipartFiles, "test"))
                .isInstanceOf(FileException.class)
                .hasMessage("파일 이름은 공백이 될 수 없습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {" .jpg", ".jpg", ".png"})
    void 확장자를_뺀_파일_이름이_널이거나_공백이면_예외가_발생한다(String originalFilename) {
        MockMultipartFile multipartFile = new MockMultipartFile("bucket", originalFilename, MediaType.TEXT_PLAIN_VALUE,
                "Hello Images".getBytes());
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        assertThatThrownBy(() -> fileSystemPhotoService.upload(multipartFiles, "test"))
                .isInstanceOf(FileException.class)
                .hasMessage("파일 이름은 공백이 될 수 없습니다");
    }

    @ParameterizedTest
    @ValueSource(strings = {"foodBowlZip", "helloWorld", "javaSpring"})
    void 확장자가_없으면_예외가_발생한다(String originalFilename) {
        MockMultipartFile multipartFile = new MockMultipartFile("bucket", originalFilename, MediaType.TEXT_PLAIN_VALUE,
                "Hello Images".getBytes());
        List<MultipartFile> multipartFiles = List.of(multipartFile);

        assertThatThrownBy(() -> fileSystemPhotoService.upload(multipartFiles, "test"))
                .isInstanceOf(FileException.class)
                .hasMessage("파일에 확장자가 존재하지 않습니다.");
    }

    private void deleteImageFile(List<String> filePaths) {
        for (String filePath : filePaths) {
            File file = new File(filePath);
            file.delete();
        }
    }
}
