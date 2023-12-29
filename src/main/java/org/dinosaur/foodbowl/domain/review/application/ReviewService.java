package org.dinosaur.foodbowl.domain.review.application;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkQueryService;
import org.dinosaur.foodbowl.domain.follow.application.FollowCustomService;
import org.dinosaur.foodbowl.domain.follow.application.dto.MemberToFollowerCountDto;
import org.dinosaur.foodbowl.domain.follow.persistence.FollowRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.domain.photo.application.PhotoService;
import org.dinosaur.foodbowl.domain.photo.domain.Photo;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.application.dto.ReviewToPhotoPathDto;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.domain.vo.ReviewFilter;
import org.dinosaur.foodbowl.domain.review.dto.request.DeviceCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewFeedPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewResponse;
import org.dinosaur.foodbowl.domain.review.exception.ReviewExceptionType;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.exception.SchoolExceptionType;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ReviewService {

    private static final int REVIEW_PHOTO_MAX_SIZE = 4;

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final SchoolRepository schoolRepository;
    private final FollowRepository followRepository;
    private final StoreService storeService;
    private final PhotoService photoService;
    private final ReviewPhotoService reviewPhotoService;
    private final ReviewCustomService reviewCustomService;
    private final ReviewPhotoCustomService reviewPhotoCustomService;
    private final FollowCustomService followCustomService;
    private final BookmarkQueryService bookmarkQueryService;

    @Transactional(readOnly = true)
    public ReviewResponse getReview(
            Long reviewId,
            Member loginMember,
            DeviceCoordinateRequest deviceCoordinateRequest
    ) {
        Review review = reviewRepository.findWithStoreAndMemberById(reviewId)
                .orElseThrow(() -> new NotFoundException(ReviewExceptionType.NOT_FOUND));
        List<Photo> reviewPhotos = reviewPhotoService.findPhotos(review);

        return ReviewResponse.of(
                review,
                followRepository.countByFollowing(review.getMember()),
                reviewPhotos,
                deviceCoordinateRequest.deviceX(),
                deviceCoordinateRequest.deviceY(),
                bookmarkQueryService.isBookmarkStoreByMember(loginMember, review.getStore())
        );
    }

    @Transactional(readOnly = true)
    public ReviewFeedPageResponse getReviewFeeds(
            Long lastReviewId,
            int pageSize,
            DeviceCoordinateRequest deviceCoordinateRequest,
            Member loginMember
    ) {
        List<Review> reviews = reviewCustomService.getReviewFeeds(lastReviewId, pageSize);
        return convertToReviewFeedResponse(reviews, loginMember, deviceCoordinateRequest);
    }

    private ReviewFeedPageResponse convertToReviewFeedResponse(
            List<Review> reviews,
            Member loginMember,
            DeviceCoordinateRequest deviceCoordinateRequest
    ) {
        MemberToFollowerCountDto memberToFollowerCountDto =
                followCustomService.getFollowerCountByMembers(getWriters(reviews));
        ReviewToPhotoPathDto reviewToPhotoPathDto = reviewPhotoCustomService.getPhotoPathByReviews(reviews);
        Set<Store> bookmarkStores = bookmarkQueryService.getBookmarkStoresByMember(loginMember);

        return ReviewFeedPageResponse.of(
                reviews,
                memberToFollowerCountDto,
                reviewToPhotoPathDto,
                bookmarkStores,
                deviceCoordinateRequest.deviceX(),
                deviceCoordinateRequest.deviceY()
        );
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse getReviewsByMemberInMapBounds(
            Long memberId,
            Long lastReviewId,
            MapCoordinateRequest mapCoordinateRequest,
            DeviceCoordinateRequest deviceCoordinateRequest,
            int pageSize,
            Member loginMember
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Review> reviews = reviewCustomService.getReviewsByMemberInMapBounds(
                member.getId(),
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
        return convertToReviewPageResponse(loginMember, reviews, deviceCoordinateRequest);
    }

    @Transactional(readOnly = true)
    public StoreReviewResponse getReviewsByStore(
            Long storeId,
            String filter,
            Long lastReviewId,
            int pageSize,
            DeviceCoordinateRequest deviceCoordinateRequest,
            Member loginMember
    ) {
        Store store = storeService.findById(storeId);
        List<Review> reviews = reviewCustomService.getReviewsByStore(
                store.getId(),
                ReviewFilter.from(filter),
                loginMember.getId(),
                lastReviewId,
                pageSize
        );

        MemberToFollowerCountDto memberToFollowerCountDto =
                followCustomService.getFollowerCountByMembers(getWriters(reviews));
        ReviewToPhotoPathDto reviewToPhotoPathDto = reviewPhotoCustomService.getPhotoPathByReviews(reviews);
        return StoreReviewResponse.of(
                store,
                reviews,
                reviewToPhotoPathDto,
                memberToFollowerCountDto,
                deviceCoordinateRequest,
                bookmarkQueryService.isBookmarkStoreByMember(loginMember, store)
        );
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse getReviewsByBookmarkInMapBounds(
            Long lastReviewId,
            MapCoordinateRequest mapCoordinateRequest,
            DeviceCoordinateRequest deviceCoordinateRequest,
            int pageSize,
            Member loginMember
    ) {
        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Review> reviews = reviewCustomService.getReviewsByBookmarkInMapBounds(
                loginMember.getId(),
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
        return convertToReviewPageResponse(loginMember, reviews, deviceCoordinateRequest);
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse getReviewsByFollowingInMapBounds(
            Long lastReviewId,
            MapCoordinateRequest mapCoordinateRequest,
            DeviceCoordinateRequest deviceCoordinateRequest,
            int pageSize,
            Member loginMember
    ) {
        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Review> reviews = reviewCustomService.getReviewsByFollowingInMapBounds(
                loginMember.getId(),
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
        return convertToReviewPageResponse(loginMember, reviews, deviceCoordinateRequest);
    }

    @Transactional(readOnly = true)
    public ReviewPageResponse getReviewsBySchoolInMapBounds(
            Long schoolId,
            Long lastReviewId,
            MapCoordinateRequest mapCoordinateRequest,
            DeviceCoordinateRequest deviceCoordinateRequest,
            int pageSize,
            Member loginMember
    ) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new NotFoundException(SchoolExceptionType.NOT_FOUND));
        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Review> reviews = reviewCustomService.getReviewsBySchoolInMapBounds(
                school.getId(),
                lastReviewId,
                mapCoordinateBoundDto,
                pageSize
        );
        return convertToReviewPageResponse(loginMember, reviews, deviceCoordinateRequest);
    }

    private MapCoordinateBoundDto convertToMapCoordinateBound(MapCoordinateRequest mapCoordinateRequest) {
        return MapCoordinateBoundDto.of(
                mapCoordinateRequest.x(),
                mapCoordinateRequest.y(),
                mapCoordinateRequest.deltaX(),
                mapCoordinateRequest.deltaY()
        );
    }

    private ReviewPageResponse convertToReviewPageResponse(
            Member loginMember,
            List<Review> reviews,
            DeviceCoordinateRequest deviceCoordinateRequest
    ) {
        MemberToFollowerCountDto memberToFollowerCountDto =
                followCustomService.getFollowerCountByMembers(getWriters(reviews));
        ReviewToPhotoPathDto reviewToPhotoPathDto = reviewPhotoCustomService.getPhotoPathByReviews(reviews);
        Set<Store> bookmarkStores = bookmarkQueryService.getBookmarkStoresByMember(loginMember);

        return ReviewPageResponse.of(
                reviews,
                memberToFollowerCountDto,
                reviewToPhotoPathDto,
                bookmarkStores,
                deviceCoordinateRequest.deviceX(),
                deviceCoordinateRequest.deviceY()
        );
    }

    private List<Member> getWriters(List<Review> reviews) {
        return reviews.stream()
                .map(Review::getMember)
                .toList();
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
        photoService.deleteAll(deletePhotos);

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

    private void saveImagesIfExists(List<MultipartFile> images, Store store, Review review) {
        if (images == null || images.isEmpty()) {
            return;
        }
        if (images.size() > REVIEW_PHOTO_MAX_SIZE) {
            throw new BadRequestException(ReviewExceptionType.PHOTO_COUNT);
        }

        List<Photo> photos = photoService.saveAll(images, store.getId().toString());
        reviewPhotoService.save(review, photos);
    }

    private void validateReviewOwner(Member member, Review review) {
        if (review.isNotOwnerOf(member)) {
            throw new BadRequestException(ReviewExceptionType.NOT_OWNER);
        }
    }
}
