package org.dinosaur.foodbowl.domain.review.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.dto.request.DeviceCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewFeedPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewResponse;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
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

    @GetMapping("/bounds")
    public ResponseEntity<ReviewPageResponse> getReviewsInMapBounds(
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "x") BigDecimal x,
            @RequestParam(name = "y") BigDecimal y,
            @RequestParam(name = "deltaX") @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaX,
            @RequestParam(name = "deltaY") @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaY,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @RequestParam(name = "category") Optional<String> category,
            @Auth LoginMember loginMember
    ) {
        MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(x, y, deltaX, deltaY);
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewPageResponse reviewPageResponse = reviewService.getReviewsInMapBounds(
                lastReviewId,
                mapCoordinateRequest,
                deviceCoordinateRequest,
                category.map(CategoryType::of)
                        .orElse(null),
                pageSize,
                loginMember
        );
        return ResponseEntity.ok(reviewPageResponse);
    }

    @GetMapping("/members")
    public ResponseEntity<ReviewPageResponse> getReviewsByMemberInMapBounds(
            @RequestParam(name = "memberId") @Positive(message = "멤버 ID는 양수만 가능합니다.") Long memberId,
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "x") BigDecimal x,
            @RequestParam(name = "y") BigDecimal y,
            @RequestParam(name = "deltaX") @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaX,
            @RequestParam(name = "deltaY") @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaY,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @RequestParam(name = "category") Optional<String> category,
            @Auth LoginMember loginMember
    ) {
        MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(x, y, deltaX, deltaY);
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewPageResponse response = reviewService.getReviewsByMemberInMapBounds(
                memberId,
                lastReviewId,
                mapCoordinateRequest,
                deviceCoordinateRequest,
                pageSize,
                category.map(CategoryType::of)
                        .orElse(null),
                loginMember
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/feeds")
    public ResponseEntity<ReviewFeedPageResponse> getReviewFeeds(
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @RequestParam(name = "pageSize", defaultValue = "20") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @Auth LoginMember loginMember
    ) {
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewFeedPageResponse reviewFeedPageResponse = reviewService.getReviewFeeds(
                lastReviewId,
                pageSize,
                deviceCoordinateRequest,
                loginMember
        );
        return ResponseEntity.ok(reviewFeedPageResponse);
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<ReviewPageResponse> getReviewsByBookmarkInMapBounds(
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "x") BigDecimal x,
            @RequestParam(name = "y") BigDecimal y,
            @RequestParam(name = "deltaX") @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaX,
            @RequestParam(name = "deltaY") @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaY,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @Auth LoginMember loginMember
    ) {
        MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(x, y, deltaX, deltaY);
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewPageResponse response = reviewService.getReviewsByBookmarkInMapBounds(
                lastReviewId,
                mapCoordinateRequest,
                deviceCoordinateRequest,
                pageSize,
                loginMember
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/following")
    public ResponseEntity<ReviewPageResponse> getReviewsByFollowingInMapBounds(
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "x") BigDecimal x,
            @RequestParam(name = "y") BigDecimal y,
            @RequestParam(name = "deltaX") @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaX,
            @RequestParam(name = "deltaY") @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaY,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @RequestParam(name = "category") Optional<String> category,
            @Auth LoginMember loginMember
    ) {
        MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(x, y, deltaX, deltaY);
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewPageResponse response = reviewService.getReviewsByFollowingInMapBounds(
                lastReviewId,
                mapCoordinateRequest,
                deviceCoordinateRequest,
                pageSize,
                category.map(CategoryType::of)
                        .orElse(null),
                loginMember
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/schools")
    public ResponseEntity<ReviewPageResponse> getReviewsBySchoolInMapBounds(
            @RequestParam(name = "schoolId") @Positive(message = "학교 ID는 양수만 가능합니다.") Long schoolId,
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "x") BigDecimal x,
            @RequestParam(name = "y") BigDecimal y,
            @RequestParam(name = "deltaX") @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaX,
            @RequestParam(name = "deltaY") @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.") BigDecimal deltaY,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @RequestParam(name = "category") Optional<String> category,
            @Auth LoginMember loginMember
    ) {
        MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(x, y, deltaX, deltaY);
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewPageResponse response = reviewService.getReviewsBySchoolInMapBounds(
                schoolId,
                lastReviewId,
                mapCoordinateRequest,
                deviceCoordinateRequest,
                category.map(CategoryType::of)
                        .orElse(null),
                pageSize,
                loginMember
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stores")
    public ResponseEntity<StoreReviewResponse> getReviewsByStore(
            @RequestParam(name = "storeId") @Positive(message = "가게 ID는 양수만 가능합니다.") Long storeId,
            @RequestParam(name = "filter", defaultValue = "ALL") String filter,
            @RequestParam(name = "lastReviewId", required = false) @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long lastReviewId,
            @RequestParam(name = "pageSize", defaultValue = "10") @Positive(message = "페이지 크기는 양수만 가능합니다.") int pageSize,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @Auth LoginMember loginMember
    ) {
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        StoreReviewResponse storeReviewResponse = reviewService.getReviewsByStore(
                storeId,
                filter,
                lastReviewId,
                pageSize,
                deviceCoordinateRequest,
                loginMember
        );
        return ResponseEntity.ok(storeReviewResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable("id") @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long reviewId,
            @RequestParam(name = "deviceX") BigDecimal deviceX,
            @RequestParam(name = "deviceY") BigDecimal deviceY,
            @Auth LoginMember loginMember
    ) {
        DeviceCoordinateRequest deviceCoordinateRequest = new DeviceCoordinateRequest(deviceX, deviceY);
        ReviewResponse reviewResponse = reviewService.getReview(reviewId, loginMember, deviceCoordinateRequest);
        return ResponseEntity.ok(reviewResponse);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> create(
            @RequestPart(name = "request") @Valid ReviewCreateRequest reviewCreateRequest,
            @RequestPart(name = "images", required = false)
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            @Auth LoginMember loginMember
    ) {
        reviewService.create(reviewCreateRequest, imageFiles, loginMember);
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
            @Auth LoginMember loginMember
    ) {
        reviewService.update(reviewId, reviewUpdateRequest, imageFiles, loginMember);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long reviewId,
            @Auth LoginMember loginMember
    ) {
        reviewService.delete(reviewId, loginMember);
        return ResponseEntity.noContent().build();
    }
}
