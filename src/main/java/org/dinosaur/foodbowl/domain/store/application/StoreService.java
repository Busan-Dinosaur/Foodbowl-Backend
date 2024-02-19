package org.dinosaur.foodbowl.domain.store.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkQueryService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.exception.MemberExceptionType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.domain.review.application.ReviewCustomService;
import org.dinosaur.foodbowl.domain.review.application.dto.MapCoordinateBoundDto;
import org.dinosaur.foodbowl.domain.review.application.dto.StoreToReviewCountDto;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.Category;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.domain.vo.CategoryType;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponses;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponses;
import org.dinosaur.foodbowl.domain.store.exception.SchoolExceptionType;
import org.dinosaur.foodbowl.domain.store.exception.StoreExceptionType;
import org.dinosaur.foodbowl.domain.store.persistence.CategoryRepository;
import org.dinosaur.foodbowl.domain.store.persistence.SchoolRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreCustomRepository;
import org.dinosaur.foodbowl.domain.store.persistence.StoreRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StoreService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final StoreCustomRepository storeCustomRepository;
    private final SchoolRepository schoolRepository;
    private final CategoryRepository categoryRepository;
    private final SchoolService schoolService;
    private final StoreSchoolService storeSchoolService;
    private final StoreCustomService storeCustomService;
    private final ReviewCustomService reviewCustomService;
    private final BookmarkQueryService bookmarkQueryService;

    @Transactional(readOnly = true)
    public Store findById(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(StoreExceptionType.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Optional<Store> findByLocationId(String locationId) {
        return storeRepository.findByLocationId(locationId);
    }

    @Transactional(readOnly = true)
    public CategoriesResponse getCategories() {
        List<Category> categories = categoryRepository.findAllByOrderById();
        return CategoriesResponse.from(categories);
    }

    @Transactional(readOnly = true)
    public StoreSearchResponses search(String name, BigDecimal x, BigDecimal y, int size) {
        Point memberCurrentPoint = PointUtils.generate(x, y);
        List<StoreSearchResponse> searchResponses =
                storeCustomRepository.search(
                                name,
                                memberCurrentPoint.getX(),
                                memberCurrentPoint.getY(),
                                size
                        )
                        .stream()
                        .toList();
        return StoreSearchResponses.from(searchResponses);
    }

    @Transactional(readOnly = true)
    public StoreMapBoundResponses getStoresInMapBounds(
            MapCoordinateRequest mapCoordinateRequest,
            LoginMember loginMember
    ) {
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Store> stores = storeCustomService.getStoresInMapBounds(mapCoordinateBoundDto);

        return convertToStoreMapBoundResponses(stores, viewer);
    }

    @Transactional(readOnly = true)
    public StoreMapBoundResponses getStoresByMemberInMapBounds(
            Long memberId,
            MapCoordinateRequest mapCoordinateRequest,
            LoginMember loginMember
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Store> stores =
                storeCustomService.getStoresByMemberInMapBounds(member.getId(), mapCoordinateBoundDto);
        return convertToStoreMapBoundResponses(stores, viewer);
    }

    @Transactional(readOnly = true)
    public StoreMapBoundResponses getStoresByBookmarkInMapBounds(
            MapCoordinateRequest mapCoordinateRequest,
            LoginMember loginMember
    ) {
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Store> stores =
                storeCustomService.getStoresByBookmarkInMapBounds(viewer.getId(), mapCoordinateBoundDto);
        return convertToStoreMapBoundResponses(stores, viewer);
    }

    @Transactional(readOnly = true)
    public StoreMapBoundResponses getStoresByFollowingInMapBounds(
            MapCoordinateRequest mapCoordinateRequest,
            LoginMember loginMember
    ) {
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Store> stores =
                storeCustomService.getStoresByFollowingInMapBounds(viewer.getId(), mapCoordinateBoundDto);
        return convertToStoreMapBoundResponses(stores, viewer);
    }

    @Transactional(readOnly = true)
    public StoreMapBoundResponses getStoresBySchoolInMapBounds(
            Long schoolId,
            MapCoordinateRequest mapCoordinateRequest,
            LoginMember loginMember
    ) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new NotFoundException(SchoolExceptionType.NOT_FOUND));
        Member viewer = memberRepository.findById(loginMember.id())
                .orElseThrow(() -> new NotFoundException(MemberExceptionType.NOT_FOUND));

        MapCoordinateBoundDto mapCoordinateBoundDto = convertToMapCoordinateBound(mapCoordinateRequest);
        List<Store> stores =
                storeCustomService.getStoresBySchoolInMapBounds(school.getId(), mapCoordinateBoundDto);
        return convertToStoreMapBoundResponses(stores, viewer);
    }

    private MapCoordinateBoundDto convertToMapCoordinateBound(MapCoordinateRequest mapCoordinateRequest) {
        return MapCoordinateBoundDto.of(
                mapCoordinateRequest.x(),
                mapCoordinateRequest.y(),
                mapCoordinateRequest.deltaX(),
                mapCoordinateRequest.deltaY()
        );
    }

    private StoreMapBoundResponses convertToStoreMapBoundResponses(List<Store> stores, Member member) {
        StoreToReviewCountDto storeToReviewCountDto = reviewCustomService.getReviewCountByStores(stores);
        Set<Store> bookmarkStores = bookmarkQueryService.getBookmarkStoresByMember(member);
        return StoreMapBoundResponses.of(stores, storeToReviewCountDto, bookmarkStores);
    }

    @Transactional
    public Store create(StoreCreateDto storeCreateDto) {
        storeRepository.findByLocationId(storeCreateDto.locationId()).ifPresent(
                existingStore -> {
                    throw new BadRequestException(StoreExceptionType.DUPLICATE);
                }
        );
        Store store = storeRepository.save(convertToStore(storeCreateDto));
        if (storeCreateDto.schoolName() != null) {
            saveSchool(
                    store,
                    storeCreateDto.schoolName(),
                    storeCreateDto.schoolAddress(),
                    storeCreateDto.schoolX(),
                    storeCreateDto.schoolY()
            );
        }
        return store;
    }

    private Store convertToStore(StoreCreateDto storeCreateDto) {
        CategoryType categoryType = CategoryType.of(storeCreateDto.category());
        Category category = categoryRepository.findById(categoryType.getId());
        Point coordinate = PointUtils.generate(storeCreateDto.storeX(), storeCreateDto.storeY());
        Address address = Address.of(storeCreateDto.address(), coordinate);
        return Store.builder()
                .locationId(storeCreateDto.locationId())
                .storeName(storeCreateDto.storeName())
                .category(category)
                .address(address)
                .storeUrl(storeCreateDto.storeUrl())
                .phone(storeCreateDto.phone())
                .build();
    }

    private void saveSchool(
            Store store,
            String schoolName,
            String schoolAddress,
            BigDecimal schoolX,
            BigDecimal schoolY
    ) {
        School school = schoolService.findByName(schoolName)
                .orElseGet(() -> schoolService.save(schoolName, schoolAddress, schoolX, schoolY));
        storeSchoolService.save(store, school);
    }
}
