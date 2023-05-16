package org.dinosaur.foodbowl.testsupport;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.photo.entity.Photo;
import org.dinosaur.foodbowl.domain.photo.repository.PhotoRepository;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PhotoTestSupport {

    private final PhotoRepository photoRepository;
    private final PostTestSupport postTestSupport;

    public PhotoBuilder builder() {
        return new PhotoBuilder();
    }

    public final class PhotoBuilder {

        private Post post;
        private String path;

        public PhotoBuilder post(Post post) {
            this.post = post;
            return this;
        }

        public PhotoBuilder path(String path) {
            this.path = path;
            return this;
        }

        public Photo build() {
            return photoRepository.save(
                    Photo.builder()
                            .post(post == null ? postTestSupport.postBuilder().build() : post)
                            .path(path == null ? "path" + UUID.randomUUID() : path)
                            .build()
            );
        }
    }
}
