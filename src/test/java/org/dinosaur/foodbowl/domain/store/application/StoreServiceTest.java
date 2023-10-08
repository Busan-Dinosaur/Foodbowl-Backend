package org.dinosaur.foodbowl.domain.store.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.util.List;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.store.application.dto.StoreCreateDto;
import org.dinosaur.foodbowl.domain.store.domain.School;
import org.dinosaur.foodbowl.domain.store.domain.Store;
import org.dinosaur.foodbowl.domain.store.domain.StoreSchool;
import org.dinosaur.foodbowl.domain.store.domain.vo.Address;
import org.dinosaur.foodbowl.domain.store.domain.vo.SchoolName;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponses;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponses;
import org.dinosaur.foodbowl.domain.store.persistence.StoreSchoolRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.InvalidArgumentException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.dinosaur.foodbowl.global.util.PointUtils;
import org.dinosaur.foodbowl.test.IntegrationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class StoreServiceTest extends IntegrationTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private StoreSchoolRepository storeSchoolRepository;

    @Nested
    class 가게_ID로_조회_시 {

        @Test
        void 등록된_가게라면_가게를_조회한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null,
                    null
            );
            Store store = storeService.create(storeCreateDtoWithoutSchool);

            Store findStore = storeService.findById(store.getId());

            assertThat(findStore).isEqualTo(store);
        }

        @Test
        void 등록되지_않은_가게라면_예외를_던진다() {
            assertThatThrownBy(() -> storeService.findById(Long.MAX_VALUE))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("일치하는 가게를 찾을 수 없습니다.");
        }
    }

    @Nested
    class 장소_ID로_조회_시 {

        @Test
        void 등록된_가게라면_가게를_조회한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null,
                    null
            );
            Store store = storeService.create(storeCreateDtoWithoutSchool);

            assertThat(storeService.findByLocationId(store.getLocationId())).isPresent();
        }

        @Test
        void 등록되지_않은_가게라면_가게가_조회되지_않는다() {
            String locationId = String.valueOf(Long.MAX_VALUE);

            assertThat(storeService.findByLocationId(locationId)).isEmpty();
        }
    }

    @Test
    void 등록된_모든_카테고리_목록을_조회한다() {
        CategoriesResponse response = storeService.getCategories();

        assertThat(response.categories()).hasSize(11);
    }

    @Nested
    class 가게_검색_시 {

        @Test
        void 단어가_포함된_가게들을_사용자_위치에서_가까운_순으로_조회한다() {
            String name = "김밥";
            BigDecimal userX = new BigDecimal("123.1667");
            BigDecimal userY = new BigDecimal("37.1245");
            Store storeA = storeTestPersister.builder()
                    .locationId("12346585")
                    .storeName("김밥천국 선릉점")
                    .address(Address.of(
                            "서울시 강남구 선릉로 4244번길 2323-124",
                            PointUtils.generate(BigDecimal.valueOf(125.142), BigDecimal.valueOf(36.241)))
                    )
                    .save();
            Store nearestStoreB = storeTestPersister.builder()
                    .locationId("915366999")
                    .storeName("얌샘김밥 선릉점")
                    .address(Address.of(
                            "서울시 강남구 선릉로 424번길 2323",
                            PointUtils.generate(userX, userY))
                    )
                    .save();
            Store storeC = storeTestPersister.builder()
                    .locationId("122355")
                    .storeName("고기듬뿍냉면")
                    .address(Address.of(
                            "서울시 강남구 헌릉로 4244번길 2323-124",
                            PointUtils.generate(BigDecimal.valueOf(125.14242), BigDecimal.valueOf(36.21141))
                    ))
                    .save();

            StoreSearchResponses storeSearchResponses = storeService.search(
                    name,
                    userX,
                    userY,
                    10
            );

            assertSoftly(softly -> {
                softly.assertThat(storeSearchResponses.searchResponses()).hasSize(2);
                softly.assertThat(storeSearchResponses.searchResponses().get(0).storeId())
                        .isEqualTo(nearestStoreB.getId());
                softly.assertThat(storeSearchResponses.searchResponses().get(1).storeId())
                        .isEqualTo(storeA.getId());
            });
        }
    }

    @Nested
    class 멤버의_리뷰가_존재하는_가게_목록_조회_시 {

        @Test
        void 존재하지_않은_멤버라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            assertThatThrownBy(() -> storeService.getStoresByMemberInMapBounds(-1L, mapCoordinateRequest, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("등록되지 않은 회원입니다.");
        }

        @Test
        void 가게_리뷰_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresByMemberInMapBounds(member.getId(), mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(result.get(0).url()).isEqualTo(store.getStoreUrl());
                softly.assertThat(result.get(0).x()).isEqualTo(store.getAddress().getCoordinate().getX());
                softly.assertThat(result.get(0).y()).isEqualTo(store.getAddress().getCoordinate().getY());
                softly.assertThat(result.get(0).reviewCount()).isEqualTo(2);
                softly.assertThat(result.get(0).isBookmarked()).isFalse();
            });
        }

        @Test
        void 북마크한_가게는_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(member).store(store).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresByMemberInMapBounds(member.getId(), mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).isBookmarked()).isTrue();
            });
        }

        @Test
        void 일치하는_가게가_없으면_빈_리스트를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresByMemberInMapBounds(member.getId(), mapCoordinateRequest, member);

            assertThat(response.stores()).isEmpty();
        }
    }

    @Nested
    class 북마크한_가게_목록_조회_시 {

        @Test
        void 가게_리뷰_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            reviewTestPersister.builder().member(writer).store(store).save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response = storeService.getStoresByBookmarkInMapBounds(mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(result.get(0).url()).isEqualTo(store.getStoreUrl());
                softly.assertThat(result.get(0).x()).isEqualTo(store.getAddress().getCoordinate().getX());
                softly.assertThat(result.get(0).y()).isEqualTo(store.getAddress().getCoordinate().getY());
                softly.assertThat(result.get(0).reviewCount()).isEqualTo(2);
                softly.assertThat(result.get(0).isBookmarked()).isTrue();
            });
        }

        @Test
        void 모든_가게_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response = storeService.getStoresByBookmarkInMapBounds(mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).isBookmarked()).isTrue();
            });
        }

        @Test
        void 일치하는_가게가_없으면_빈_리스트를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response = storeService.getStoresByBookmarkInMapBounds(mapCoordinateRequest, member);

            assertThat(response.stores()).isEmpty();
        }
    }

    @Nested
    class 팔로잉_유저의_리뷰가_존재하는_가게_목록_조회_시 {

        @Test
        void 가게_리뷰_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            reviewTestPersister.builder().member(writer).store(store).save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresByFollowingInMapBounds(mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(result.get(0).url()).isEqualTo(store.getStoreUrl());
                softly.assertThat(result.get(0).x()).isEqualTo(store.getAddress().getCoordinate().getX());
                softly.assertThat(result.get(0).y()).isEqualTo(store.getAddress().getCoordinate().getY());
                softly.assertThat(result.get(0).reviewCount()).isEqualTo(2);
                softly.assertThat(result.get(0).isBookmarked()).isFalse();
            });
        }

        @Test
        void 북마크한_가게는_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            followTestPersister.builder().following(writer).follower(member).save();
            Store store = storeTestPersister.builder().save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            reviewTestPersister.builder().member(writer).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresByFollowingInMapBounds(mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).isBookmarked()).isTrue();
            });
        }

        @Test
        void 일치하는_가게가_없으면_빈_리스트를_반환한다() {
            Member member = memberTestPersister.builder().save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(123.3636),
                    BigDecimal.valueOf(32.3131),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresByFollowingInMapBounds(mapCoordinateRequest, member);

            assertThat(response.stores()).isEmpty();
        }
    }

    @Nested
    class 학교_근처_가게_목록_조회_시 {

        @Test
        void 존재하지_않은_학교라면_예외를_던진다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            assertThatThrownBy(() -> storeService.getStoresBySchoolInMapBounds(-1L, mapCoordinateRequest, member))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessage("존재하지 않는 학교입니다.");
        }

        @Test
        void 가게_리뷰_수도_함께_조회한다() {
            Member member = memberTestPersister.builder().save();
            Member writer = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            reviewTestPersister.builder().member(writer).store(store).save();
            reviewTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresBySchoolInMapBounds(school.getId(), mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).id()).isEqualTo(store.getId());
                softly.assertThat(result.get(0).name()).isEqualTo(store.getStoreName());
                softly.assertThat(result.get(0).categoryName()).isEqualTo(store.getCategory().getName());
                softly.assertThat(result.get(0).addressName()).isEqualTo(store.getAddress().getAddressName());
                softly.assertThat(result.get(0).url()).isEqualTo(store.getStoreUrl());
                softly.assertThat(result.get(0).x()).isEqualTo(store.getAddress().getCoordinate().getX());
                softly.assertThat(result.get(0).y()).isEqualTo(store.getAddress().getCoordinate().getY());
                softly.assertThat(result.get(0).reviewCount()).isEqualTo(2);
                softly.assertThat(result.get(0).isBookmarked()).isFalse();
            });
        }

        @Test
        void 북마크한_가게는_북마크_여부가_TRUE_이다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            storeSchoolTestPersister.builder().store(store).school(school).save();
            bookmarkTestPersister.builder().member(member).store(store).save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresBySchoolInMapBounds(school.getId(), mapCoordinateRequest, member);

            List<StoreMapBoundResponse> result = response.stores();
            assertSoftly(softly -> {
                softly.assertThat(result).hasSize(1);
                softly.assertThat(result.get(0).isBookmarked()).isTrue();
            });
        }

        @Test
        void 일치하는_가게가_없으면_빈_리스트를_반환한다() {
            Member member = memberTestPersister.builder().save();
            Store store = storeTestPersister.builder().save();
            School school = schoolTestPersister.builder().save();
            MapCoordinateRequest mapCoordinateRequest = new MapCoordinateRequest(
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getX()),
                    BigDecimal.valueOf(store.getAddress().getCoordinate().getY()),
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1)
            );

            StoreMapBoundResponses response =
                    storeService.getStoresBySchoolInMapBounds(school.getId(), mapCoordinateRequest, member);

            assertThat(response.stores()).isEmpty();
        }
    }

    @Nested
    class 가게_생성_시 {

        @Test
        void 학교_없이_생성한다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null,
                    null
            );

            Store store = storeService.create(storeCreateDtoWithoutSchool);

            assertSoftly(softly -> {
                softly.assertThat(store.getId()).isNotNull();
                softly.assertThat(store.getLocationId()).isEqualTo(storeCreateDtoWithoutSchool.locationId());
                softly.assertThat(store.getStoreName()).isEqualTo(storeCreateDtoWithoutSchool.storeName());
                softly.assertThat(store.getCategory().getCategoryType().name())
                        .isEqualTo(storeCreateDtoWithoutSchool.category());
            });
        }

        @Test
        void 학교와_함께_생성한다() {
            StoreCreateDto storeCreateDtoWithSchool = generateStoreCreateDto(
                    "부산대학교",
                    "부산광역시 금정구 부산대학로63번길 2",
                    BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234)
            );

            Store store = storeService.create(storeCreateDtoWithSchool);
            List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchool_Name(new SchoolName("부산대학교"));
            List<Store> stores = storeSchools.stream()
                    .map(StoreSchool::getStore)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(store.getId()).isNotNull();
                softly.assertThat(store.getStoreName()).isEqualTo(storeCreateDtoWithSchool.storeName());
                softly.assertThat(store.getCategory().getCategoryType().name())
                        .isEqualTo(storeCreateDtoWithSchool.category());
                softly.assertThat(stores).contains(store);
            });
        }

        @Test
        void 이미_존재하는_학교인_경우도_학교와_함께_생성한다() {
            StoreCreateDto storeCreateDtoWithSchool = generateStoreCreateDto(
                    "부산대학교",
                    "부산광역시 금정구 부산대학로63번길 2",
                    BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234)
            );
            Store store1 = storeService.create(storeCreateDtoWithSchool);
            StoreCreateDto nextStoreCreateDto = new StoreCreateDto(
                    "9999999",
                    "용용선생 선릉점",
                    "중식",
                    "서울시 강남구 선릉로 424번길 2323",
                    BigDecimal.valueOf(123.1232134),
                    BigDecimal.valueOf(37.45433545),
                    "http://images2.foodbowl",
                    "02-2141-4567",
                    "부산대학교",
                    "부산광역시 금정구 부산대학로63번길 2",
                    BigDecimal.valueOf(123.12),
                    BigDecimal.valueOf(37.1234));

            Store store2 = storeService.create(nextStoreCreateDto);
            List<StoreSchool> storeSchools = storeSchoolRepository.findAllBySchool_Name(new SchoolName("부산대학교"));
            List<Store> stores = storeSchools.stream()
                    .map(StoreSchool::getStore)
                    .toList();

            assertSoftly(softly -> {
                softly.assertThat(stores).contains(store1, store2);
                softly.assertThat(stores).hasSize(2);
            });
        }

        @Test
        void 이미_등록된_가게라면_예외를_던진다() {
            StoreCreateDto storeCreateDtoWithoutSchool = generateStoreCreateDto(
                    null,
                    null,
                    null,
                    null
            );
            storeService.create(storeCreateDtoWithoutSchool);

            assertThatThrownBy(() -> storeService.create(storeCreateDtoWithoutSchool))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("이미 존재하는 가게입니다.");
        }

        @ParameterizedTest
        @ValueSource(strings = {"!부산대학교", "@서울대학교@", "+연세대학교-", "!@#!$"})
        void 학교_이름이_정상적이지_않으면_예외를_던진다(String schoolName) {
            StoreCreateDto storeCreateDto = new StoreCreateDto(
                    "1234567",
                    "농민백암순대",
                    "한식",
                    "서울시 강남구 선릉로 14번길 245",
                    BigDecimal.valueOf(123.124),
                    BigDecimal.valueOf(37.4545),
                    "http://images.foodbowl",
                    "02-123-4567",
                    schoolName,
                    null,
                    null,
                    null
            );

            assertThatThrownBy(() -> storeService.create(storeCreateDto))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("학교 이름 형식이 잘못되었습니다.");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"미국식", "한국식", "학식", "급식"})
        void 카테고리_타입이_존재하지_않으면_예외가_발생한다(String category) {
            StoreCreateDto storeCreateDto = new StoreCreateDto(
                    "21415511",
                    "농민백암순대",
                    category,
                    "서울시 강남구 선릉로 14번길 245",
                    BigDecimal.valueOf(123.124),
                    BigDecimal.valueOf(37.4545),
                    "http://images.foodbowl",
                    "02-123-4567",
                    null,
                    null,
                    null,
                    null
            );

            assertThatThrownBy(() -> storeService.create(storeCreateDto))
                    .isInstanceOf(InvalidArgumentException.class)
                    .hasMessage("일치하는 카테고리를 찾을 수 없습니다.");
        }
    }

    private StoreCreateDto generateStoreCreateDto(
            String schoolName,
            String schoolAddress,
            BigDecimal schoolX,
            BigDecimal schoolY
    ) {
        return new StoreCreateDto(
                "12314535",
                "농민백암순대",
                "한식",
                "서울시 강남구 선릉로 14번길 245",
                BigDecimal.valueOf(123.124),
                BigDecimal.valueOf(37.4545),
                "http://images.foodbowl",
                "02-123-4567",
                schoolName,
                schoolAddress,
                schoolX,
                schoolY
        );
    }
}
