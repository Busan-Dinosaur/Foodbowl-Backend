package org.dinosaur.foodbowl.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileTestUtils {

    private static final String TEST_FILE_UPLOAD_PATH = "src/test/resources/bucket";

    public static List<MultipartFile> generateMultipartFiles(int size) {
        List<MultipartFile> multipartFiles = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            multipartFiles.add(new MockMultipartFile(
                    "images",
                    "foodBowl.jpg",
                    MediaType.TEXT_PLAIN_VALUE,
                    "Hello Images".getBytes()));
        }
        return multipartFiles;
    }

    public static MultipartFile generateMockMultiPartFile() {
        return new MockMultipartFile(
                "images",
                "foodBowl.jpg",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello Images".getBytes());
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
