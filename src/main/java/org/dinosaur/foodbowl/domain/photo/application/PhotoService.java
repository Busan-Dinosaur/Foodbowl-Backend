package org.dinosaur.foodbowl.domain.photo.application;

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
    private final PhotoManager photoManager;

    @Transactional
    public Photo save(MultipartFile file, String workingDirectory) {
        String imagePath = photoManager.upload(file, workingDirectory);
        Photo photo = convertToPhoto(imagePath);
        return photoRepository.save(photo);
    }

    private Photo convertToPhoto(String imagePath) {
        return Photo.builder()
                .path(imagePath)
                .build();
    }

    @Transactional
    public void delete(Photo photo) {
        photoRepository.deleteByPhoto(photo);
        photoManager.delete(photo.getPath());
    }
}
