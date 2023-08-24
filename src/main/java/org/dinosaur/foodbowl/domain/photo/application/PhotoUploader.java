package org.dinosaur.foodbowl.domain.photo.application;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PhotoUploader {

    List<String> upload(List<MultipartFile> images, String parentDirectory);
}
