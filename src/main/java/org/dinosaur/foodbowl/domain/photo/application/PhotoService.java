package org.dinosaur.foodbowl.domain.photo.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.persistence.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final PhotoUploader photoUploader;

    @Transactional
    public List<Photo> save(List<MultipartFile> images, String parentDirectory) {
        List<String> imagePaths = photoUploader.upload(images, parentDirectory);
        List<Photo> photos = imagePaths.stream()
                .map(this::convertToPhoto)
                .toList();

        for (Photo photo : photos) {
            photoRepository.save(photo);
        }
        return photos;
    }

    private Photo convertToPhoto(String imagePath) {
        return Photo.builder()
                .path(imagePath)
                .build();
    }
}
