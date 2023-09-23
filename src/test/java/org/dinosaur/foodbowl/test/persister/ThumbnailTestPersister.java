package org.dinosaur.foodbowl.test.persister;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.domain.Thumbnail;
import org.dinosaur.foodbowl.domain.photo.persistence.ThumbnailRepository;

@RequiredArgsConstructor
@Persister
public class ThumbnailTestPersister {

    private final ThumbnailRepository thumbnailRepository;

    public ThumbnailBuilder builder() {
        return new ThumbnailBuilder();
    }

    public final class ThumbnailBuilder {

        private String path;

        public ThumbnailBuilder path(String path) {
            this.path = path;
            return this;
        }

        public Thumbnail save() {
            Thumbnail thumbnail = Thumbnail.builder()
                    .path(path == null ? "http://justdoeat.shop/static/images/thumbnail.png" : path)
                    .build();
            return thumbnailRepository.save(thumbnail);
        }
    }
}
