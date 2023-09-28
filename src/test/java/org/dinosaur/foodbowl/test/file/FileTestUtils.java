package org.dinosaur.foodbowl.test.file;

import static org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType.WRITE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileTestUtils {

    private static final String TEST_FILE_UPLOAD_PATH = "src/test/resources/bucket";
    private static final byte[] IMAGE = generateMockImage();

    public static byte[] generateMockImage() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new FileException(WRITE);
        }
    }

    public static MultipartFile generateMultiPartFile(String name) {
        return new MockMultipartFile(
                name,
                "foodBowl.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                IMAGE
        );
    }

    public static List<MultipartFile> generateMultipartFiles(int size, String name) {
        List<MultipartFile> multipartFiles = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            MultipartFile file = generateMultiPartFile(name);
            multipartFiles.add(file);
        }
        return multipartFiles;
    }

    public static void cleanUp() {
        deleteFolder(TEST_FILE_UPLOAD_PATH);
    }

    private static void deleteFolder(String path) {
        File folder = new File(path);
        if (folder.exists()) {
            deleteFileRecursive(folder);
        }
    }

    private static void deleteFileRecursive(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }

            for (File child : files) {
                deleteFileRecursive(child);
            }
        }
        file.delete();
    }
}
