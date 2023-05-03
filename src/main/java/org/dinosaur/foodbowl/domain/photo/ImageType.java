package org.dinosaur.foodbowl.domain.photo;

public enum ImageType {
    PHOTO("/photo/", false, -1, -1),
    THUMBNAIL("/thumbnail/", true, 450, 450);

    private final String path;
    private final boolean resizable;
    private final int width;
    private final int height;

    ImageType(String path, boolean resizable, int width, int height) {
        this.path = path;
        this.resizable = resizable;
        this.width = width;
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public boolean isResizable() {
        return resizable;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
