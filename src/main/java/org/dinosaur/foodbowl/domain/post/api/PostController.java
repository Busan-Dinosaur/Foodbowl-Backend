package org.dinosaur.foodbowl.domain.post.api;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.post.application.PostService;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private final PostService postService;

    @GetMapping("/thumbnails")
    public ResponseEntity<PageResponse<PostThumbnailResponse>> findThumbnailsInProfile(
            @RequestParam Long memberId,
            @PageableDefault(size = 18, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        PageResponse response = postService.findThumbnailsInProfile(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}
