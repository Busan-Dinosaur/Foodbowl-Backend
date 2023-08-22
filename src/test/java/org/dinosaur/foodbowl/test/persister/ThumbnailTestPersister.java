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
        private Integer width;
        private Integer height;

        public ThumbnailBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ThumbnailBuilder width(Integer width) {
            this.width = width;
            return this;
        }

        public ThumbnailBuilder height(Integer height) {
            this.height = height;
            return this;
        }

        public Thumbnail save() {
            Thumbnail thumbnail = Thumbnail.builder()
                    .path(path == null ? "http://justdoeat.shop/static/images/thumbnail.png" : path)
                    .width(width == null ? 100 : width)
                    .height(height == null ? 100 : height)
                    .build();
            return thumbnailRepository.save(thumbnail);
        }
    }
}
