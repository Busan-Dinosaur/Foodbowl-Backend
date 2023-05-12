package org.dinosaur.foodbowl.domain.photo;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ThumbnailUtils extends ImageUtils {

    private static final String dir = "/thumbnail/";
    private static final int NEW_WIDTH = 450;

    @Override
    public String storeImageFile(MultipartFile file) throws IOException {
        validateEmptyFile(file);
        BufferedImage inputImage = ImageIO.read(file.getInputStream());
        String originalFilename = file.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);
        String path = dir + storeFilename;
        String fullPath = getFullPath(path);

        if (inputImage.getWidth() <= NEW_WIDTH) {
            file.transferTo(Path.of(fullPath));
            return path;
        }

        BufferedImage newImage = drawNewImage(inputImage);
        String format = extractExt(originalFilename);
        storeImage(fullPath, format, newImage);
        return path;
    }

    private BufferedImage drawNewImage(BufferedImage inputImage) {
        int originWidth = inputImage.getWidth();
        int originHeight = inputImage.getHeight();
        int newHeight = (originHeight * NEW_WIDTH) / originWidth;

        BufferedImage newImage = new BufferedImage(NEW_WIDTH, newHeight, BufferedImage.TYPE_INT_RGB);
        Image resizeImage = inputImage.getScaledInstance(NEW_WIDTH, newHeight, Image.SCALE_FAST);

        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(resizeImage, 0, 0, null);
        graphics.dispose();
        return newImage;
    }

    private void storeImage(String fullPath, String format, BufferedImage newImage) throws IOException {
        File newFile = new File(fullPath);
        ImageIO.write(newImage, format, newFile);
    }
}
