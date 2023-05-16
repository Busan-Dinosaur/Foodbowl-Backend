package org.dinosaur.foodbowl.domain.photo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public abstract class ImageUtils {

    private static final Set<String> imageExtensions = Set.of("jpeg", "jpg", "png", "webp");
    private static final String DOT = ".";

    @Value("${file.dir}")
    protected String fileDir;

    public ImageUtils() {
    }

    public ImageUtils(final String fileDir) {
        this.fileDir = fileDir;
    }

    public List<String> storeImageFiles(List<MultipartFile> files) {
        List<String> storedImagesPaths = new ArrayList<>();

        for (MultipartFile file : files) {
            storedImagesPaths.add(storeImageFile(file));
        }
        return storedImagesPaths;
    }

    public abstract String storeImageFile(MultipartFile file);

    protected void storeFile(final String fullPath, final MultipartFile file) {
        try {
            file.transferTo(Path.of(fullPath));
        } catch (IOException e) {
            throw new FoodbowlException(ErrorStatus.IMAGE_IO_EXCEPTION);
        }
    }

    protected String createStoreFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String extension = extractExtension(originalFilename);
        return uuid + DOT + extension;
    }

    protected String extractExtension(String originalFilename) {
        int extensionIndex = originalFilename.lastIndexOf(DOT) + 1;
        return originalFilename.substring(extensionIndex).toLowerCase();
    }

    protected String getFullPath(String imagePath) {
        return fileDir + imagePath;
    }

    public void deleteImageFiles(List<String> imagePaths) {
        imagePaths.forEach(this::deleteImageFile);
    }

    public void deleteImageFile(String imagePath) {
        File file = new File(getFullPath(imagePath));
        file.delete();
    }

    protected void validateImageFile(MultipartFile file) {
        validateEmptyFile(file);
        validateImageType(file);
    }

    private void validateEmptyFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FoodbowlException(ErrorStatus.IMAGE_NOT_FOUND);
        }
    }

    private void validateImageType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        if (imageExtensions.contains(extension)) {
            return;
        }
        throw new FoodbowlException(ErrorStatus.IMAGE_INVALID_EXTENSION);
    }
}
