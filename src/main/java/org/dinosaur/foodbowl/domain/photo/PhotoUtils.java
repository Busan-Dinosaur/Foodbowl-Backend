package org.dinosaur.foodbowl.domain.photo;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoUtils extends ImageUtils {

    private static final String dir = "/photo/";

    public PhotoUtils() {
    }

    public PhotoUtils(final String fileDir) {
        super(fileDir);
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        validateImageFile(file);

        String originalFilename = file.getOriginalFilename();
        String storeFileName = createStoreFilename(originalFilename);
        String fullPath = getFullPath(dir + storeFileName);

        storeFile(fullPath, file);
        return fullPath;
    }
}
