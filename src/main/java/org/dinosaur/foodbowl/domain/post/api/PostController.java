package org.dinosaur.foodbowl.domain.post.api;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.POST_INVALID_FILE_SIZE;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.post.application.PostService;
import org.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequest;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@RestController
public class PostController {

    private static final int MAX_FILE_SIZE = 10;
    private static final int MIN_FILE_SIZE = 1;

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(
            @MemberId Long memberId,
            @RequestPart(value = "request", required = true) @Valid PostCreateRequest postCreateRequest,
            @RequestPart(value = "imageFiles", required = true) List<MultipartFile> imageFiles
    ) {
        validateFileSize(imageFiles);
        Long postId = postService.save(memberId, postCreateRequest, imageFiles);
        return ResponseEntity.created(URI.create("/api/v1/posts/" + postId)).build();
    }

    private void validateFileSize(final List<MultipartFile> imageFiles) {
        int size = imageFiles.size();
        if (size > MAX_FILE_SIZE || size < MIN_FILE_SIZE) {
            throw new FoodbowlException(POST_INVALID_FILE_SIZE);
        }
    }

    @GetMapping("/thumbnails")
    public ResponseEntity<PageResponse<PostThumbnailResponse>> findThumbnailsInProfile(
            @RequestParam Long memberId,
            @PageableDefault(size = 18, page = 0, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        PageResponse<PostThumbnailResponse> response = postService.findThumbnailsInProfile(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}
