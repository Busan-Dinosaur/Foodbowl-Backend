package org.dinosaur.foodbowl.domain.photo;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ThumbnailUtils extends ImageUtils {

    private static final String dir = "/thumbnail/";
    private static final int DEFAULT_TOP_LEFT_X = 0;
    private static final int DEFAULT_TOP_LEFT_Y = 0;

    public ThumbnailUtils() {
    }

    public ThumbnailUtils(final String fileDir) {
        super(fileDir);
    }

    @Override
    public String storeImageFile(MultipartFile file) {
        validateImageFile(file);
        BufferedImage inputImage = getImageFrom(file);
        String originalFilename = file.getOriginalFilename();
        String storeFilename = createStoreFilename(originalFilename);
        String fullPath = getFullPath(dir + storeFilename);

        if (ThumbnailSize.isBelongTo(inputImage)) {
            storeFile(fullPath, file);
            return fullPath;
        }

        BufferedImage newImage = resizeImage(inputImage);
        String format = extractExtension(originalFilename);
        storeNewImageFile(fullPath, format, newImage);
        return fullPath;
    }

    public BufferedImage getImageFrom(final MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new FoodbowlException(ErrorStatus.IMAGE_IO_EXCEPTION);
        }
    }

    private BufferedImage resizeImage(BufferedImage inputImage) {
        ThumbnailSize thumbnailSize = ThumbnailSize.of(inputImage);
        Integer newWidth = thumbnailSize.width();
        Integer newHeight = thumbnailSize.height();

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);

        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(resizeImage, DEFAULT_TOP_LEFT_X, DEFAULT_TOP_LEFT_Y, null);
        graphics.dispose();
        return newImage;
    }

    private void storeNewImageFile(String fullPath, String format, BufferedImage newImage) {
        File newFile = new File(fullPath);
        try {
            ImageIO.write(newImage, format, newFile);
        } catch (IOException e) {
            throw new FoodbowlException(ErrorStatus.IMAGE_IO_EXCEPTION);
        }
    }
}
