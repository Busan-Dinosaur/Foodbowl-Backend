package org.dinosaur.foodbowl.domain.photo;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoUtils extends ImageUtils {

    private static final String dir = "/photo/";

    @Override
    public String storeImageFile(MultipartFile file) {
        validateImageFile(file);

        String originalFilename = file.getOriginalFilename();
        String storeFileName = createStoreFilename(originalFilename);
        String path = dir + storeFileName;
        String fullPath = getFullPath(path);

        storeFile(fullPath, file);
        return path;
    }
}
