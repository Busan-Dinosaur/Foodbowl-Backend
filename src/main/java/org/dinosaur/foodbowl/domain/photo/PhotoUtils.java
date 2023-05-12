package org.dinosaur.foodbowl.domain.photo;

import java.io.IOException;
import java.nio.file.Path;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class PhotoUtils extends ImageUtils {

    private static final String dir = "/photo/";

    @Override
    public String storeImageFile(MultipartFile file) throws IOException {
        validateEmptyFile(file);

        String originalFilename = file.getOriginalFilename();
        String storeFileName = createStoreFilename(originalFilename);
        String path = dir + storeFileName;
        String fullPath = getFullPath(path);

        file.transferTo(Path.of(fullPath));
        return path;
    }
}
