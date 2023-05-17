package org.dinosaur.foodbowl.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentRequest {

    @Positive
    @NotNull(message = "게시글 ID는 반드시 포함되어야 합니다.")
    private Long postId;

    @NotBlank(message = "댓글 내용은 반드시 포함되어야 합니다.")
    private String message;
}
