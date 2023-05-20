package org.dinosaur.foodbowl.domain.bookmark.api;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkService;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
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
@RequestMapping("/api/v1/bookmarks")
@RestController
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping("/thumbnails")
    public ResponseEntity<PageResponse<BookmarkThumbnailResponse>> findThumbnailsInProfile(
            @RequestParam Long memberId,
            @PageableDefault(page = 0, size = 18, sort = "createdAt", direction = Direction.DESC) Pageable pageable
    ) {
        PageResponse<BookmarkThumbnailResponse> response = bookmarkService.findThumbnailsInProfile(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}
