package org.dinosaur.foodbowl.domain.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public abstract class ImageUtils {

    @Value("${file.dir}")
    protected String fileDir = "foodbowl-res";

    public List<String> storeImageFiles(List<MultipartFile> files) {
        return files.stream()
                .map(file -> {
                    try {
                        return storeImageFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public abstract String storeImageFile(MultipartFile file) throws IOException;

    protected String createStoreFilename(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    protected String extractExt(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf(".");
        return originalFilename.substring(dotIndex + 1);
    }

    protected String getFullPath(String imagePath) {
        return fileDir + imagePath;
    }

    public void deleteImageFiles(List<String> imagePaths) {
        if (imagePaths == null) {
            return;
        }
        imagePaths.forEach(this::deleteImageFile);
    }

    public void deleteImageFile(String imagePath) {
        File file = new File(getFullPath(imagePath));
        file.delete();
    }

    protected void validateEmptyFile(MultipartFile file) throws FileNotFoundException {
        if (file.isEmpty()) {
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
        }
    }
}
