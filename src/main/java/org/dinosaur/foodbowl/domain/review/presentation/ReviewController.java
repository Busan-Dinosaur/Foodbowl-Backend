package org.dinosaur.foodbowl.domain.review.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@RestController
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> create(
            @RequestPart(name = "request") @Valid ReviewCreateRequest reviewCreateRequest,
            @RequestPart(name = "images", required = false)
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            @Auth Member member
    ) {
        reviewService.create(reviewCreateRequest, imageFiles, member);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(
            path = "/{id}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<Void> update(
            @PathVariable @Positive(message = "ID는 양수만 가능합니다.") Long id,
            @RequestPart(name = "request") @Valid ReviewUpdateRequest reviewUpdateRequest,
            @RequestPart(name = "images", required = false)
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            @Auth Member member
    ) {
        reviewService.update(id, reviewUpdateRequest, imageFiles, member);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "ID는 양수만 가능합니다.") Long id,
            @Auth Member member
    ) {
        reviewService.delete(id, member);
        return ResponseEntity.noContent().build();
    }
}
