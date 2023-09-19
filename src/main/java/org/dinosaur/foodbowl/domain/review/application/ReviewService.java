package org.dinosaur.foodbowl.domain.review.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.application.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
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
    private final StoreService storeService;
    private final PhotoService photoService;
    private final ReviewPhotoService reviewPhotoService;

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
