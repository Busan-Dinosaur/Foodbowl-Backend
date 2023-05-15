package org.dinosaur.foodbowl.domain.post.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.post.entity.Post;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostThumbnailResponse {

    private Long postId;
    private String thumbnailPath;
    private LocalDateTime createdAt;

    public static PostThumbnailResponse from(Post post) {
        return new PostThumbnailResponse(
                post.getId(),
                post.getThumbnail().getPath(),
                post.getCreatedAt()
        );
    }
}
