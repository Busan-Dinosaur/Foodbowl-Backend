package org.dinosaur.foodbowl.domain.review.presentation;

import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.file.FileTestUtils;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .email("foodBowl@gmail.com")
                .socialId("foodBowlId")
                .socialType(SocialType.APPLE)
                .nickname("foodbowl")
                .introduction("푸드볼 서비스")
                .build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));
    }

    @Nested
    class 리뷰_작성_시_ {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 사진이_포함된_경우_200_상태코드를_반환한다() throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), anyList(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .file(multipartFile1)
                            .file(multipartFile2)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 사진이_없는_경우_200_상태코드를_반환한다() throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 사진이_4개_보다_많은_경우_400_상태코드를_반환한다() throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), anyList(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();
            MockMultipartFile multipartFile3 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();
            MockMultipartFile multipartFile4 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();
            MockMultipartFile multipartFile5 = (MockMultipartFile) FileTestUtils.generateMockMultiPartFile();

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .file(multipartFile1)
                            .file(multipartFile2)
                            .file(multipartFile3)
                            .file(multipartFile4)
                            .file(multipartFile5)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value("사진의 개수는 최대 4개까지 가능합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_위치_ID가_없는_경우_400_상태코드를_반환한다(String locationId) throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    locationId,
                    "신천직화집",
                    "서울시 송파구 올림픽대로 87, 1층",
                    BigDecimal.valueOf(126.12345),
                    BigDecimal.valueOf(35.1241521616),
                    "https://image.kakao.com",
                    "02-1234-2424",
                    "한식",
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("장소 ID는 반드시 포함되어야 합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_이름이_없는_경우_400_상태코드를_반환한다(String storeName) throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "12412515",
                    storeName,
                    "서울시 송파구 올림픽대로 87, 1층",
                    BigDecimal.valueOf(126.12345),
                    BigDecimal.valueOf(35.1241521616),
                    "https://image.kakao.com",
                    "02-1234-2424",
                    "한식",
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("가게 이름은 반드시 포함되어야 합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_주소가_없는_경우_400_상태코드를_반환한다(String address) throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "12312414",
                    "국민연금공단",
                    address,
                    BigDecimal.valueOf(126.12345),
                    BigDecimal.valueOf(35.1241521616),
                    "https://image.kakao.com",
                    "02-1234-2424",
                    "한식",
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("가게 주소는 반드시 포함되어야 합니다."));
        }

        @Test
        void 가게_경도가_없는_경우_400_상태코드를_반환한다() throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "12251521",
                    "국민연금공단",
                    "서울시 송파구 올림픽대로 87, 1층",
                    null,
                    BigDecimal.valueOf(35.1241521616),
                    "https://image.kakao.com",
                    "02-1234-2424",
                    "한식",
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("가게 경도는 반드시 포함되어야 합니다."));
        }

        @Test
        void 가게_위도가_없는_경우_400_상태코드를_반환한다() throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "134125",
                    "국민연금공단",
                    "서울시 송파구 올림픽대로 87, 1층",
                    BigDecimal.valueOf(135.1241521616),
                    null,
                    "https://image.kakao.com",
                    "02-1234-2424",
                    "한식",
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("가게 위도는 반드시 포함되어야 합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_정보_URL_없는_경우_400_상태코드를_반환한다(String storeUrl) throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "3241414",
                    "국민연금공단",
                    "서울시 송파구 올림픽대로 87, 1층",
                    BigDecimal.valueOf(135.1241521616),
                    BigDecimal.valueOf(35.1616),
                    storeUrl,
                    "02-1234-2424",
                    "한식",
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("카카오 서버의 가게 정보 url 반드시 포함되어야 합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_카테고리_정보가_없는_경우_400_상태코드를_반환한다(String category) throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "124121524",
                    "국민연금공단",
                    "서울시 송파구 올림픽대로 87, 1층",
                    BigDecimal.valueOf(135.1241521616),
                    BigDecimal.valueOf(35.1616),
                    "https://image.kakao.com",
                    "02-1234-2424",
                    category,
                    "가성비가 매우 좋아요",
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("가게 카테고리는 반드시 포함되어야 합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_리뷰_내용이_없는_경우_400_상태코드를_반환한다(String content) throws Exception {
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);
            ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest(
                    "1234125",
                    "국민연금공단",
                    "서울시 송파구 올림픽대로 87, 1층",
                    BigDecimal.valueOf(135.1241521616),
                    BigDecimal.valueOf(35.1616),
                    "https://image.kakao.com",
                    "02-1234-2424",
                    "한식",
                    content,
                    null,
                    null,
                    null);
            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value("가게 리뷰는 반드시 포함되어야 합니다."));
        }
    }

    private ReviewCreateRequest generateReviewCreateDto() {
        return new ReviewCreateRequest(
                "141241",
                "국민연금공단",
                "서울시 송파구 올림픽대로 87, 1층",
                BigDecimal.valueOf(126.12345),
                BigDecimal.valueOf(35.1241521616),
                "https://image.kakao.com",
                "02-1234-2424",
                "한식",
                "가성비가 매우 좋아요",
                null,
                null,
                null
        );
    }
}
