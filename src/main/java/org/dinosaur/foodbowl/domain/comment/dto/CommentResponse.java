package org.dinosaur.foodbowl.domain.comment.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.entity.Comment;
import org.dinosaur.foodbowl.domain.post.entity.Post;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponse {

    private Long commentId;
    private Long postId;
    private Long memberId;
    private String memberNickname;
    private String memberThumbnailPath;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse of(Comment comment, Post post) {
        return new CommentResponse(
                comment.getId(),
                post.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getMember().getThumbnail().getPath(),
                comment.getMessage(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
