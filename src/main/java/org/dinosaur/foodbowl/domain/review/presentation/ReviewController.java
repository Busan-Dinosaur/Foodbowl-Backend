package org.dinosaur.foodbowl.domain.review.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/v1/reviews")
@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> create(
            @RequestPart(name = "request") @Valid ReviewCreateRequest reviewCreateRequest,
            @RequestPart(name = "images", required = false) List<MultipartFile> imageFiles,
            @Auth Member member
    ) {
        Long reviewId = reviewService.create(reviewCreateRequest, imageFiles, member);
        return ResponseEntity.created(URI.create("/v1/reviews/" + reviewId)).build();
    }
}
