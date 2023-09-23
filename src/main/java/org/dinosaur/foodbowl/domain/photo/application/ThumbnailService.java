package org.dinosaur.foodbowl.domain.photo.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.domain.photo.persistence.ThumbnailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ThumbnailService {

    private static final String THUMBNAIL_PATH = "thumbnails";

    private final ThumbnailRepository thumbnailRepository;
    private final PhotoManager photoManager;

    @Transactional
    public Thumbnail save(MultipartFile file) {
        String thumbnailPath = photoManager.upload(file, THUMBNAIL_PATH);
        Thumbnail thumbnail = convertToThumbnail(thumbnailPath);
        return thumbnailRepository.save(thumbnail);
    }

    private Thumbnail convertToThumbnail(String thumbnailPath) {
        return Thumbnail.builder()
                .path(thumbnailPath)
                .build();
    }

    @Transactional
    public void delete(Thumbnail thumbnail) {
        thumbnailRepository.deleteByThumbnail(thumbnail);
        photoManager.delete(thumbnail.getPath());
    }
}
