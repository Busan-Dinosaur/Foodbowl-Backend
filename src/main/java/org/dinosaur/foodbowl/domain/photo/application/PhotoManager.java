package org.dinosaur.foodbowl.domain.photo.application;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoManager {

    String upload(MultipartFile image, String parentDirectory);

    void delete(String path);
}
