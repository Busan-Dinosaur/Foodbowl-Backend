package org.dinosaur.foodbowl.domain.photo;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import org.dinosaur.foodbowl.global.exception.ErrorStatus;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ThumbnailUtils extends ImageUtils {

    private static final String dir = "/thumbnail/";
    private static final int SIZE = 450;
    private static final int DEFAULT_TOP_LEFT_X = 0;
    private static final int DEFAULT_TOP_LEFT_Y = 0;
    private static final int WIDTH_INDEX = 0;
    private static final int HEIGHT_INDEX = 1;

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

        if (inputImage.getWidth() <= SIZE && inputImage.getHeight() <= SIZE) {
            storeFile(fullPath, file);
            return fullPath;
        }

        BufferedImage newImage = drawNewImage(inputImage);
        String format = extractExtension(originalFilename);
        storeNewFile(fullPath, format, newImage);
        return fullPath;
    }

    private BufferedImage getImageFrom(final MultipartFile file) {
        try {
            return ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new FoodbowlException(ErrorStatus.IMAGE_IO_EXCEPTION);
        }
    }

    private BufferedImage drawNewImage(BufferedImage inputImage) {
        List<Integer> imageSize = calculateImageSize(inputImage);
        int newWidth = imageSize.get(WIDTH_INDEX);
        int newHeight = imageSize.get(HEIGHT_INDEX);

        BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);

        Graphics graphics = newImage.getGraphics();
        graphics.drawImage(resizeImage, DEFAULT_TOP_LEFT_X, DEFAULT_TOP_LEFT_Y, null);
        graphics.dispose();
        return newImage;
    }

    private List<Integer> calculateImageSize(BufferedImage inputImage) {
        int originWidth = inputImage.getWidth();
        int originHeight = inputImage.getHeight();
        if (originWidth >= originHeight) {
            int newWidth = SIZE;
            int newHeight = (originHeight * SIZE) / originWidth;
            return List.of(newWidth, newHeight);
        }
        int newHeight = SIZE;
        int newWidth = (originWidth * SIZE) / originHeight;
        return List.of(newWidth, newHeight);
    }

    private void storeNewFile(String fullPath, String format, BufferedImage newImage) {
        File newFile = new File(fullPath);
        try {
            ImageIO.write(newImage, format, newFile);
        } catch (IOException e) {
            throw new FoodbowlException(ErrorStatus.IMAGE_IO_EXCEPTION);
        }
    }
}
