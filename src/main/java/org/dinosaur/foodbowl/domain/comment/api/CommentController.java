package org.dinosaur.foodbowl.domain.comment.api;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(
            @Valid @RequestBody CommentCreateRequest commentCreateRequest,
            @MemberId Long memberId
    ) {
        commentService.save(memberId, commentCreateRequest);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + commentCreateRequest.getPostId())).build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @MemberId Long memberId,
            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest
    ) {
        commentService.update(commentId, memberId, commentUpdateRequest);
        return ResponseEntity.noContent().build();
    }
}
