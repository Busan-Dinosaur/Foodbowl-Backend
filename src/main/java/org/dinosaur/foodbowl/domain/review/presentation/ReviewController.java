package org.dinosaur.foodbowl.domain.review.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.dto.request.CoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.PaginationReviewResponse;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@RestController
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @GetMapping("/following")
    public ResponseEntity<PaginationReviewResponse> getPaginationReviewsByFollowing(
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @Valid CoordinateRequest coordinateRequest,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @Auth Member loginMember
    ) {
        PaginationReviewResponse response =
                reviewService.getPaginationReviewsByFollowing(lastReviewId, coordinateRequest, pageSize, loginMember);
        return ResponseEntity.ok(response);
    }

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
            @PathVariable("id") @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long reviewId,
            @RequestPart(name = "request") @Valid ReviewUpdateRequest reviewUpdateRequest,
            @RequestPart(name = "images", required = false)
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            @Auth Member member
    ) {
        reviewService.update(reviewId, reviewUpdateRequest, imageFiles, member);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long reviewId,
            @Auth Member member
    ) {
        reviewService.delete(reviewId, member);
        return ResponseEntity.noContent().build();
    }
}
