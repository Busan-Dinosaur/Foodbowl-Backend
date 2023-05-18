package org.dinosaur.foodbowl.domain.comment.api;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentResponse;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @Valid @RequestBody CommentCreateRequest commentCreateRequest,
            @MemberId Long memberId) {
        CommentResponse commentResponse = commentService.save(memberId, commentCreateRequest);
        return ResponseEntity.created(URI.create("/comments/" + commentResponse.getId()))
                .body(commentResponse);
    }
}
