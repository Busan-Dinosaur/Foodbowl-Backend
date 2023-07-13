package org.dinosaur.foodbowl.domain.comment.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentResponse;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
import org.dinosaur.foodbowl.dto.PageResponse;
import org.dinosaur.foodbowl.resolver.MemberId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<PageResponse<CommentResponse>> findAllComments(
            @RequestParam @Positive(message = "게시글 ID는 양수만 가능합니다.") Long postId,
            @PageableDefault(page = 0, size = 18, sort = "createdAt", direction = Direction.ASC) Pageable pageable
    ) {
        PageResponse<CommentResponse> commentResponses = commentService.findAllCommentsInPost(postId, pageable);
        return ResponseEntity.ok(commentResponses);
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

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable @Positive(message = "댓글 ID는 양수만 가능합니다.") Long commentId,
            @MemberId Long memberId
    ) {
        commentService.deleteComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }
}
