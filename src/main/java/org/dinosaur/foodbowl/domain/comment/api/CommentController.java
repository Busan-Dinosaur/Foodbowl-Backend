package org.dinosaur.foodbowl.domain.comment.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @RequestBody @Valid CommentCreateRequest commentCreateRequest,
            @MemberId Long memberId
    ) {
        commentService.save(memberId, commentCreateRequest);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + commentCreateRequest.getPostId())).build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable @Positive(message = "댓글 ID는 양수만 가능합니다.") Long commentId,
            @MemberId Long memberId,
            @RequestBody @Valid CommentUpdateRequest commentUpdateRequest
    ) {
        commentService.updateComment(commentId, memberId, commentUpdateRequest);
        return ResponseEntity.noContent().build();
    }
}
