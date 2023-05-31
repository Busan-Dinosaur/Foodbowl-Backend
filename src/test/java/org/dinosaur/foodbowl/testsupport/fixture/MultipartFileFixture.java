package org.dinosaur.foodbowl.testsupport.fixture;

import java.io.FileInputStream;
import java.io.IOException;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.mock.web.MockMultipartFile;

public class MultipartFileFixture {

    public static MockMultipartFile get() {
        final String savedPath = "src/test/resources/test_image/testImage.jpg";

        try {
            FileInputStream fileInputStream = new FileInputStream(savedPath);
            return new MockMultipartFile(
                    "imageFiles",
                    "testImage.jpg",
                    "image/jpeg",
                    fileInputStream
            );
        } catch (IOException e) {
            throw new FoodbowlException(ErrorStatus.IMAGE_IO_EXCEPTION);
        }
    }
}
