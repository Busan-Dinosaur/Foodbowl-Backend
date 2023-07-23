package org.dinosaur.foodbowl.domain.bookmark.api;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkService;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
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

    @GetMapping("/markers")
    public ResponseEntity<List<BookmarkStoreMarkerResponse>> findBookmarkStoreMarkers(@MemberId Long memberId) {
        List<BookmarkStoreMarkerResponse> response = bookmarkService.findBookmarkStoreMarkers(memberId);
        return ResponseEntity.ok(response);
    }
}
