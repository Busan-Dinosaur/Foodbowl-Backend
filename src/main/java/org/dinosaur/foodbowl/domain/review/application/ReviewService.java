package org.dinosaur.foodbowl.domain.review.application;

import static org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType.NOT_FOUND_ERROR;
import static org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType.NOT_OWNER_ERROR;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreService storeService;
    private final PhotoService photoService;
    private final ReviewPhotoService reviewPhotoService;

    @Transactional
    public Long create(ReviewCreateRequest reviewCreateRequest, List<MultipartFile> imageFiles, Member member) {
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
        return review.getId();
    }

    private void saveImagesIfExists(List<MultipartFile> images, Store store, Review review) {
        if (images == null || images.isEmpty()) {
            return;
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
    public void delete(Long id, Member member) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_ERROR));

        if (review.isNotOwnerOf(member)) {
            throw new BadRequestException(NOT_OWNER_ERROR);
        }

        List<Photo> photos = reviewPhotoService.findPhotos(review);

        reviewPhotoService.delete(review);
        photoService.delete(photos);
        reviewRepository.delete(review);
    }
}
