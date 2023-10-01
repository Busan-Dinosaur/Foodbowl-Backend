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

    private final PhotoManager photoManager;
    private final PhotoRepository photoRepository;

    @Transactional
    public Photo save(MultipartFile file, String workingDirectory) {
        String imagePath = photoManager.upload(file, workingDirectory);
        return savePhoto(imagePath);
    }

    private Photo savePhoto(String imagePath) {
        Photo photo = Photo.builder()
                .path(imagePath)
                .build();
        return photoRepository.save(photo);
    }

    @Transactional
    public List<Photo> saveAll(List<MultipartFile> files, String workingDirectory) {
        return files.stream()
                .map(file -> photoManager.upload(file, workingDirectory))
                .map(this::savePhoto)
                .toList();
    }

    @Transactional
    public void delete(Photo photo) {
        photoRepository.deleteByPhoto(photo);
        photoManager.delete(photo.getPath());
    }

    @Transactional
    public void deleteAll(List<Photo> photos) {
        photoRepository.deleteAllByPhotos(photos);
        deleteAllPhotoFiles(photos);
    }

    private void deleteAllPhotoFiles(List<Photo> photos) {
        photos.stream()
                .map(Photo::getPath)
                .forEach(photoManager::delete);
    }
}
