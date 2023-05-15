package org.dinosaur.foodbowl.testsupport;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.photo.repository.ThumbnailRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ThumbnailTestSupport {

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

        public Thumbnail build() {
            return thumbnailRepository.save(
                    Thumbnail.builder()
                            .path(path == null ? "path" + UUID.randomUUID() : path)
                            .width(width == null ? 300 : width)
                            .height(height == null ? 300 : height)
                            .build()
            );
        }
    }
}
