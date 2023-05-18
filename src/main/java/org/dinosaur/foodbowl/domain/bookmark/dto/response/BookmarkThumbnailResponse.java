package org.dinosaur.foodbowl.domain.bookmark.dto.response;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.entity.Bookmark;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookmarkThumbnailResponse {

    private Long bookmarkId;
    private Long postId;
    private String thumbnailPath;
    private LocalDateTime createdAt;

    public static BookmarkThumbnailResponse from(Bookmark bookmark) {
        return new BookmarkThumbnailResponse(
                bookmark.getId(),
                bookmark.getPost().getId(),
                bookmark.getPost().getThumbnail().getPath(),
                bookmark.getCreatedAt()
        );
    }
}
