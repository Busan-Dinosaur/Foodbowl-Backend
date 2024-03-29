package org.dinosaur.foodbowl.domain.bookmark.presentation;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkService;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/bookmarks")
@RestController
public class BookmarkController implements BookmarkControllerDocs {

    private final BookmarkService bookmarkService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestParam @Positive(message = "가게 ID는 양수만 가능합니다.") Long storeId,
            @Auth LoginMember loginMember
    ) {
        bookmarkService.save(storeId, loginMember);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @RequestParam @Positive(message = "가게 ID는 양수만 가능합니다.") Long storeId,
            @Auth LoginMember loginMember
    ) {
        bookmarkService.delete(storeId, loginMember);
        return ResponseEntity.noContent().build();
    }
}
