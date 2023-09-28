package org.dinosaur.foodbowl.domain.review.application;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.domain.Bookmark;
import org.dinosaur.foodbowl.domain.bookmark.persistence.BookmarkRepository;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.follow.persistence.dto.MemberFollowerCountDto;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.CoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.dto.request.CoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.PaginationReviewResponse;
import org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewCustomRepository;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewPhotoRepository;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.review.persistence.dto.ReviewPhotoPathDto;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private static final int REVIEW_PHOTO_MAX_SIZE = 4;
    private final ReviewRepository reviewRepository;
    private final ReviewCustomRepository reviewCustomRepository;
    private final FollowRepository followRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final StoreService storeService;
    private final PhotoService photoService;
    private final ReviewPhotoService reviewPhotoService;

    @Transactional(readOnly = true)
    public PaginationReviewResponse getPaginationReviewsByFollowing(
            Long lastReviewId,
            CoordinateRequest request,
            int pageSize,
            Member loginMember
    ) {
        CoordinateBoundDto coordinateBoundDto =
                CoordinateBoundDto.of(request.x(), request.y(), request.deltaX(), request.deltaY());
        List<Review> reviews = reviewCustomRepository.getPaginationReviewsByFollowing(
                loginMember.getId(),
                lastReviewId,
                coordinateBoundDto,
                pageSize
        );

        Map<Long, Long> memberIdFollowerCountGroup = getMemberFollowerCountGroup(reviews);
        Map<Long, List<String>> reviewIdPhotoPathsGroup = getReviewPhotoPathsGroup(reviews);
        Set<Long> bookmarkStoreIds = getBookmarkStoreIds(loginMember);

        return PaginationReviewResponse.of(reviews, memberIdFollowerCountGroup, reviewIdPhotoPathsGroup,
                bookmarkStoreIds);
    }

    private Map<Long, Long> getMemberFollowerCountGroup(List<Review> reviews) {
        List<Member> writers = extractReviewWriters(reviews);
        List<MemberFollowerCountDto> memberFollowerCounts = followRepository.getFollowerCountByMembers(writers);
        return memberFollowerCounts.stream()
                .collect(Collectors.toMap(
                        MemberFollowerCountDto::memberId,
                        MemberFollowerCountDto::followerCount,
                        (exist, replace) -> replace,
                        HashMap::new
                ));
    }

    private List<Member> extractReviewWriters(List<Review> reviews) {
        return reviews.stream()
                .map(Review::getMember)
                .distinct()
                .toList();
    }

    private Map<Long, List<String>> getReviewPhotoPathsGroup(List<Review> reviews) {
        List<ReviewPhotoPathDto> reviewPhotoPaths = reviewPhotoRepository.getPhotoPathByReviews(reviews);
        return reviewPhotoPaths.stream()
                .collect(Collectors.groupingBy(
                        ReviewPhotoPathDto::reviewId,
                        Collectors.mapping(ReviewPhotoPathDto::photoPath, Collectors.toList())
                ));
    }

    private Set<Long> getBookmarkStoreIds(Member member) {
        List<Bookmark> bookmarks = bookmarkRepository.findByMember(member);
        return bookmarks.stream()
                .map(Bookmark::getStore)
                .map(Store::getId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public Review create(ReviewCreateRequest reviewCreateRequest, List<MultipartFile> imageFiles, Member member) {
        StoreCreateDto storeCreateDto = convertStoreCreateDto(reviewCreateRequest);

        Store store = storeService.findByLocationId(reviewCreateRequest.locationId())
                .orElseGet(() -> storeService.create(storeCreateDto));

        Review review = reviewRepository.save(
                Review.builder()
                        .store(store)
                        .member(member)
                        .content(reviewCreateRequest.reviewContent())
                        .build()
        );

        saveImagesIfExists(imageFiles, store, review);
        return review;
    }

    private void saveImagesIfExists(List<MultipartFile> images, Store store, Review review) {
        if (images == null || images.isEmpty()) {
            return;
        }

        if (images.size() > REVIEW_PHOTO_MAX_SIZE) {
            throw new BadRequestException(ReviewExceptionType.PHOTO_COUNT);
        }

        List<Photo> photos = photoService.save(images, store.getId().toString());
        reviewPhotoService.save(review, photos);
    }

    private StoreCreateDto convertStoreCreateDto(ReviewCreateRequest reviewCreateRequest) {
        return new StoreCreateDto(
                reviewCreateRequest.locationId(),
                reviewCreateRequest.storeName(),
                reviewCreateRequest.category(),
                reviewCreateRequest.storeAddress(),
                reviewCreateRequest.x(),
                reviewCreateRequest.y(),
                reviewCreateRequest.storeUrl(),
                reviewCreateRequest.phone(),
                reviewCreateRequest.schoolName(),
                reviewCreateRequest.schoolAddress(),
                reviewCreateRequest.schoolX(),
                reviewCreateRequest.schoolY()
        );
    }

    @Transactional
    public void update(
            Long id,
            ReviewUpdateRequest reviewUpdateRequest,
            List<MultipartFile> imageFiles,
            Member member
    ) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ReviewExceptionType.NOT_FOUND));
        validateReviewOwner(member, review);

        List<Photo> currentPhotos = reviewPhotoService.findPhotos(review);
        List<Long> deletePhotoIds = reviewUpdateRequest.deletePhotoIds();
        validatePhotoSize(imageFiles, currentPhotos, deletePhotoIds);
        validateDeletePhotoIds(currentPhotos, deletePhotoIds);

        List<Photo> deletePhotos = extractPhotosForDelete(currentPhotos, deletePhotoIds);
        reviewPhotoService.deleteByReviewAndPhoto(review, deletePhotos);
        photoService.delete(deletePhotos);

        saveImagesIfExists(imageFiles, review.getStore(), review);
        review.updateContent(reviewUpdateRequest.reviewContent());
    }

    private void validatePhotoSize(
            List<MultipartFile> images,
            List<Photo> currentPhotos,
            List<Long> deletePhotoIds
    ) {
        if (images == null || images.isEmpty()) {
            return;
        }
        int insertReviewPhotoSize = images.size();
        int currentReviewPhotoSize = currentPhotos.size();
        int deleteReviewPhotoSize = deletePhotoIds.size();

        int updateReviewPhotoSize = currentReviewPhotoSize - deleteReviewPhotoSize + insertReviewPhotoSize;
        if (updateReviewPhotoSize > REVIEW_PHOTO_MAX_SIZE) {
            throw new BadRequestException(ReviewExceptionType.PHOTO_COUNT);
        }
    }

    private void validateDeletePhotoIds(List<Photo> currentPhotos, List<Long> deletePhotoIds) {
        Set<Long> currentPhotoIds = currentPhotos.stream()
                .map(Photo::getId)
                .collect(Collectors.toSet());

        if (!currentPhotoIds.containsAll(deletePhotoIds)) {
            throw new BadRequestException(ReviewExceptionType.INVALID_PHOTO);
        }
    }

    private List<Photo> extractPhotosForDelete(List<Photo> currentPhotos, List<Long> deletePhotoIds) {
        Set<Long> deleteCandidateIds = new HashSet<>(deletePhotoIds);
        return currentPhotos.stream()
                .filter(photo -> deleteCandidateIds.contains(photo.getId()))
                .toList();
    }

    @Transactional
    public void delete(Long id, Member member) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ReviewExceptionType.NOT_FOUND));

        validateReviewOwner(member, review);

        List<Photo> photos = reviewPhotoService.findPhotos(review);

        reviewPhotoService.deleteByReviewAndPhoto(review, photos);
        reviewRepository.delete(review);
    }

    private void validateReviewOwner(Member member, Review review) {
        if (review.isNotOwnerOf(member)) {
            throw new BadRequestException(ReviewExceptionType.NOT_OWNER);
        }
    }
}
