package org.dinosaur.foodbowl.domain.photo;

import java.awt.image.BufferedImage;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public record ThumbnailSize(Integer width, Integer height) {

    private static final int DEFAULT_SIZE = 450;

    public static ThumbnailSize of(MultipartFile file) {
        ThumbnailUtils thumbnailUtils = new ThumbnailUtils();
        thumbnailUtils.validateImageFile(file);
        BufferedImage image = thumbnailUtils.getImageFrom(file);

        return of(image);
    }

    public static ThumbnailSize of(BufferedImage image) {
        int originWidth = image.getWidth();
        int originHeight = image.getHeight();
        if (originWidth >= originHeight) {
            int newWidth = DEFAULT_SIZE;
            int newHeight = (originHeight * DEFAULT_SIZE) / originWidth;
            return new ThumbnailSize(newWidth, newHeight);
        }
        int newHeight = DEFAULT_SIZE;
        int newWidth = (originWidth * DEFAULT_SIZE) / originHeight;
        return new ThumbnailSize(newWidth, newHeight);
    }

    public static boolean isBelongTo(BufferedImage image) {
        return image.getWidth() <= DEFAULT_SIZE && image.getHeight() <= DEFAULT_SIZE;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ThumbnailSize thumbnailSize = (ThumbnailSize) o;
        return Objects.equals(width, thumbnailSize.width) && Objects.equals(height, thumbnailSize.height);
    }
}
