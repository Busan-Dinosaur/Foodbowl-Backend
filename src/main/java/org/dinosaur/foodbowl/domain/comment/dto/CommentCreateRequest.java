package org.dinosaur.foodbowl.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {

    @Positive(message = "게시글 ID는 음수가 될 수 없습니다.")
    @NotNull(message = "게시글 ID는 반드시 포함되어야 합니다.")
    private Long postId;

    @Size(min = 1, max = 255, message = "댓글은 최소 1자, 최대 255자까지 가능합니다.")
    @NotBlank(message = "댓글 내용은 반드시 포함되어야 합니다.")
    private String message;
}
