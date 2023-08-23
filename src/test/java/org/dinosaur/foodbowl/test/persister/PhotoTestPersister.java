package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.photo.persistence.PhotoRepository;

@RequiredArgsConstructor
@Persister
public class PhotoTestPersister {

    private final PhotoRepository photoRepository;

    public PhotoBuilder builder() {
        return new PhotoBuilder();
    }

    public final class PhotoBuilder  {

        private String path;

        public PhotoBuilder path(String path) {
            this.path = path;
            return this;
        }

        public Photo save() {
            Photo photo = Photo.builder()
                    .path(path == null ? String.valueOf(System.currentTimeMillis()) : path)
                    .build();
            return photoRepository.save(photo);
        }
    }
}
