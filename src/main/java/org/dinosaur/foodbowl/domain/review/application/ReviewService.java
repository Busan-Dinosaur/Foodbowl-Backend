package org.dinosaur.foodbowl.domain.review.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.application.PhotoUtils;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.dto.StoreCreateDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreService storeService;
    private final PhotoService photoService;
    private final PhotoUtils photoUtils;

    @Transactional
    public Long create(ReviewCreateRequest reviewCreateRequest, List<MultipartFile> imageFiles, Member member) {
        StoreCreateDto storeCreateDto = convertStoreCreateDto(reviewCreateRequest);
        Store store = storeService.create(storeCreateDto);

        Review review = reviewRepository.save(
                Review.builder()
                        .store(store)
                        .member(member)
                        .content(reviewCreateRequest.getReviewContent())
                        .build()
        );

        saveImagesIfExists(imageFiles, store, review);
        return review.getId();
    }

    private void saveImagesIfExists(List<MultipartFile> images, Store store, Review review) {
        if (images == null) {
            return;
        }
        List<String> imagePaths = photoUtils.upload(images, store.getStoreName());
        List<Photo> photos = imagePaths.stream()
                .map(imagePath -> Photo.builder()
                        .review(review)
                        .path(imagePath)
                        .build())
                .toList();
        photoService.save(photos);
    }

    private StoreCreateDto convertStoreCreateDto(ReviewCreateRequest reviewCreateRequest) {
        return new StoreCreateDto(
                reviewCreateRequest.getStoreName(),
                reviewCreateRequest.getCategory(),
                reviewCreateRequest.getStoreAddress(),
                reviewCreateRequest.getX(),
                reviewCreateRequest.getY(),
                reviewCreateRequest.getStoreUrl(),
                reviewCreateRequest.getPhone(),
                reviewCreateRequest.getSchoolName(),
                reviewCreateRequest.getSchoolX(),
                reviewCreateRequest.getSchoolY()
        );
    }
}