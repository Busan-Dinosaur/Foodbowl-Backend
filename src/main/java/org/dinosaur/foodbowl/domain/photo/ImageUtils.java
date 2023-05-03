package org.dinosaur.foodbowl.domain.photo;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUtils {

    @Value("${file.dir}")
    private String fileDir;

    public List<String> storeImageFiles(ImageType imageType, List<MultipartFile> files)
        throws IOException {
        List<String> storedImagesPaths = new ArrayList<>();

        for (MultipartFile file : files) {
            storedImagesPaths.add(storeImageFile(imageType, file));
        }
        return storedImagesPaths;
    }

    public String storeImageFile(ImageType imageType, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new FileNotFoundException("파일이 존재하지 않습니다.");
        }
        String originalFilename = file.getOriginalFilename();
        String storeFileName = createStoreFileName(originalFilename);
        String path = imageType.getPath() + storeFileName;
        String fullPath = getFullPath(path);

        if (imageType.isResizable()) {
            BufferedImage image = ImageIO.read(file.getInputStream());
            image = resizeImage(image, imageType.getWidth(), imageType.getHeight());
            ImageIO.write(image, extractExt(originalFilename), new File(fullPath));
            return path;
        }

        file.transferTo(new File(fullPath));
        return path;
    }

    private String createStoreFileName(String originalFilename) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalFilename);
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf(".");
        return originalFilename.substring(dotIndex + 1);
    }

    private String getFullPath(String path) {
        return fileDir + path;
    }

    private BufferedImage resizeImage(BufferedImage bi, int width, int height) {
        return Scalr.resize(bi, Scalr.Method.AUTOMATIC, Mode.AUTOMATIC, width, height,
            Scalr.OP_ANTIALIAS);
    }

    public void deleteImageFiles(List<String> paths) {
        if (paths == null) {
            return;
        }
        for (String path : paths) {
            deleteImageFile(path);
        }
    }

    public void deleteImageFile(String path) {
        File file = new File(getFullPath(path));
        file.delete();
    }
}
