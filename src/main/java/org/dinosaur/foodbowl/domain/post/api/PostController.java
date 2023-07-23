package org.dinosaur.foodbowl.domain.post.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.post.application.PostService;
import org.dinosaur.foodbowl.domain.post.dto.response.PostStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.global.common.response.PageResponse;
import org.dinosaur.foodbowl.global.presentation.MemberId;
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
            @PageableDefault(page = 0, size = 18, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PageResponse<PostThumbnailResponse> response = postService.findThumbnailsInProfile(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/thumbnails/latest")
    public ResponseEntity<PageResponse<PostThumbnailResponse>> findLatestThumbnails(
            @PageableDefault(page = 0, size = 18, sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PageResponse<PostThumbnailResponse> response = postService.findLatestThumbnails(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/markers")
    public ResponseEntity<List<PostStoreMarkerResponse>> findPostStoreMarkers(@MemberId Long memberId) {
        List<PostStoreMarkerResponse> response = postService.findPostStoreMarkers(memberId);
        return ResponseEntity.ok(response);
    }
}
