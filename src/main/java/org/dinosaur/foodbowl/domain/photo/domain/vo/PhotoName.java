package org.dinosaur.foodbowl.domain.photo.domain.vo;

import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.exception.FileExceptionType;
import org.dinosaur.foodbowl.global.exception.FileException;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhotoName {

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpeg", "jpg", "png", "webp");
    private static final String UNDER_BAR = "_";
    private static final String DOT = ".";

    public static String of(String originalFilename) {
        validateFileName(originalFilename);
        return convertNameToPath(originalFilename);
    }

    private static String convertNameToPath(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (extension == null) {
            throw new FileException(FileExceptionType.FILE_FORMAT);
        }
        String fileBaseName = UUID.randomUUID().toString().substring(0, 8);
        validateFileName(fileBaseName);
        validateExtension(extension);

        return fileBaseName + UNDER_BAR + System.currentTimeMillis() + DOT + extension;
    }

    private static void validateFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FileException(FileExceptionType.FILE_BASE_NAME);
        }
    }

    private static void validateExtension(String extension) {
        if (!IMAGE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new FileException(FileExceptionType.FILE_EXTENSION);
        }
    }
}
