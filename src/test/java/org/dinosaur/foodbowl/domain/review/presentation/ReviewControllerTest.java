package org.dinosaur.foodbowl.domain.review.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.domain.Review;
import org.dinosaur.foodbowl.domain.review.dto.request.DeviceCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewContentResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageInfo;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewStoreResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewWriterResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewContentResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(ReviewController.class)
class ReviewControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Nested
    class 멤버_리뷰_페이징_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewPageResponse response = new ReviewPageResponse(
                    List.of(
                            new ReviewResponse(
                                    new ReviewWriterResponse(1L, "hello", "image.png", 0L),
                                    new ReviewContentResponse(
                                            1L,
                                            "content",
                                            List.of("image.png"),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                    ),
                                    new ReviewStoreResponse(
                                            1L,
                                            "카페",
                                            "가게",
                                            "가게주소",
                                            100.13,
                                            false
                                    )
                            )
                    ),
                    new ReviewPageInfo(10L, 1L, 10)
            );
            given(reviewService.getReviewsByMemberInMapBounds(
                    anyLong(),
                    any(),
                    any(MapCoordinateRequest.class),
                    any(DeviceCoordinateRequest.class),
                    anyInt(),
                    any(Member.class)
            )).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ReviewPageResponse result = objectMapper.readValue(jsonResponse, ReviewPageResponse.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 멤버_ID가_양수가_아니라면_400_응답을_반환한다(String memberId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", memberId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("멤버 ID는 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 마지막_리뷰ID가_양수가_아니라면_400_응답을_반환한다(String lastReviewId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("lastReviewId", lastReviewId)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("리뷰 ID는 양수만 가능합니다.")));
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memerId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY)
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @Test
        void 디바이스_경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 디바이스_위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_양수가_아니라면_400_응답을_반환한다(String pageSize) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636")
                            .param("pageSize", pageSize))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("페이지 크기는 양수만 가능합니다.")));
        }
    }

    @Nested
    class 가게에_해당하는_리뷰_페이징_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 필터링_조건_없이_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            StoreReviewResponse response = new StoreReviewResponse(
                    List.of(
                            new StoreReviewContentResponse(
                                    new ReviewWriterResponse(
                                            1L,
                                            "그레이",
                                            "https://static.image.com",
                                            10
                                    ),
                                    new ReviewContentResponse(
                                            1L,
                                            "맛있어요",
                                            List.of("https://static.image1.com"),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                    )
                            )
                    ),
                    new ReviewPageInfo(1L, 1L, 1)
            );
            given(reviewService.getReviewsByStore(
                    anyLong(),
                    any(),
                    any(),
                    anyInt(),
                    any(Member.class)
            )).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/reviews/stores")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("storeId", "1")
                            .param("pageSize", "20")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "35.324"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreReviewResponse result = objectMapper.readValue(jsonResponse, StoreReviewResponse.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @Test
        void 필터링_조건과_함께_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            StoreReviewResponse response = new StoreReviewResponse(
                    List.of(
                            new StoreReviewContentResponse(
                                    new ReviewWriterResponse(
                                            1L,
                                            "그레이",
                                            "https://static.image.com",
                                            10
                                    ),
                                    new ReviewContentResponse(
                                            1L,
                                            "맛있어요",
                                            List.of("https://static.image1.com"),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                    )
                            )
                    ),
                    new ReviewPageInfo(1L, 1L, 1)
            );
            given(reviewService.getReviewsByStore(
                    anyLong(),
                    any(),
                    any(),
                    anyInt(),
                    any(Member.class)
            )).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/reviews/stores")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("storeId", "1")
                            .param("filter", "FRIEND")
                            .param("pageSize", "20")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "35.324"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreReviewResponse result = objectMapper.readValue(jsonResponse, StoreReviewResponse.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 가게_ID가_양수가_아니라면_400_응답을_반환한다(String storeId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/stores")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("storeId", storeId)
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 ID는 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 마지막_리뷰ID가_양수가_아니라면_400_응답을_반환한다(String lastReviewId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/stores")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("storeId", "1")
                            .param("lastReviewId", lastReviewId)
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("리뷰 ID는 양수만 가능합니다.")));
        }

        @Test
        void 가게_ID가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/stores")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("deviceX", "123.12412")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_양수가_아니라면_400_응답을_반환한다(String pageSize) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/stores")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("storeId", "1")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636")
                            .param("pageSize", pageSize))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("페이지 크기는 양수만 가능합니다.")));
        }
    }

    @Nested
    class 북마크한_가게_리뷰_페이징_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewPageResponse response = new ReviewPageResponse(
                    List.of(
                            new ReviewResponse(
                                    new ReviewWriterResponse(1L, "hello", "image.png", 0L),
                                    new ReviewContentResponse(
                                            1L,
                                            "content",
                                            List.of("image.png"),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                    ),
                                    new ReviewStoreResponse(
                                            1L,
                                            "카페",
                                            "가게",
                                            "가게주소",
                                            100.13,
                                            false
                                    )
                            )
                    ),
                    new ReviewPageInfo(10L, 1L, 10)
            );
            given(reviewService.getReviewsByBookmarkInMapBounds(
                    any(),
                    any(MapCoordinateRequest.class),
                    any(DeviceCoordinateRequest.class),
                    anyInt(),
                    any(Member.class)
            )).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ReviewPageResponse result = objectMapper.readValue(jsonResponse, ReviewPageResponse.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 마지막_리뷰ID가_양수가_아니라면_400_응답을_반환한다(String lastReviewId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("lastReviewId", lastReviewId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("리뷰 ID는 양수만 가능합니다.")));
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY)
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @Test
        void 디바이스_경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 디바이스_위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_양수가_아니라면_400_응답을_반환한다(String pageSize) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636")
                            .param("pageSize", pageSize))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("페이지 크기는 양수만 가능합니다.")));
        }
    }

    @Nested
    class 팔로잉_멤버의_리뷰_페이징_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewPageResponse response = new ReviewPageResponse(
                    List.of(
                            new ReviewResponse(
                                    new ReviewWriterResponse(1L, "hello", "image.png", 0L),
                                    new ReviewContentResponse(
                                            1L,
                                            "content",
                                            List.of("image.png"),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                    ),
                                    new ReviewStoreResponse(
                                            1L,
                                            "카페",
                                            "가게",
                                            "가게주소",
                                            100.13,
                                            false
                                    )
                            )
                    ),
                    new ReviewPageInfo(10L, 1L, 10)
            );
            given(reviewService.getReviewsByFollowingInMapBounds(
                    any(),
                    any(MapCoordinateRequest.class),
                    any(DeviceCoordinateRequest.class),
                    anyInt(),
                    any(Member.class)
            )).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ReviewPageResponse result = objectMapper.readValue(jsonResponse, ReviewPageResponse.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 마지막_리뷰ID가_양수가_아니라면_400_응답을_반환한다(String lastReviewId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("lastReviewId", lastReviewId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("리뷰 ID는 양수만 가능합니다.")));
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY)
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @Test
        void 디바이스_경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 디바이스_위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_양수가_아니라면_400_응답을_반환한다(String pageSize) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/following")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636")
                            .param("pageSize", pageSize))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("페이지 크기는 양수만 가능합니다.")));
        }
    }

    @Nested
    class 학교_근처_가게_리뷰_페이징_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewPageResponse response = new ReviewPageResponse(
                    List.of(
                            new ReviewResponse(
                                    new ReviewWriterResponse(1L, "hello", "image.png", 0L),
                                    new ReviewContentResponse(
                                            1L,
                                            "content",
                                            List.of("image.png"),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                            LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
                                    ),
                                    new ReviewStoreResponse(
                                            1L,
                                            "카페",
                                            "가게",
                                            "가게주소",
                                            100.13,
                                            false
                                    )
                            )
                    ),
                    new ReviewPageInfo(10L, 1L, 10)
            );
            given(reviewService.getReviewsBySchoolInMapBounds(
                    anyLong(),
                    any(),
                    any(MapCoordinateRequest.class),
                    any(DeviceCoordinateRequest.class),
                    anyInt(),
                    any(Member.class)
            )).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            ReviewPageResponse result = objectMapper.readValue(jsonResponse, ReviewPageResponse.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 학교ID가_양수가_아니라면_400_응답을_반환한다(String schoolId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", schoolId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("학교 ID는 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 마지막_리뷰ID가_양수가_아니라면_400_응답을_반환한다(String lastReviewId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("lastReviewId", lastReviewId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("리뷰 ID는 양수만 가능합니다.")));
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY)
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @Test
        void 디바이스_경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceY", "32.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 디바이스_위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 페이지_크기가_양수가_아니라면_400_응답을_반환한다(String pageSize) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/reviews/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12")
                            .param("deviceX", "123.3636")
                            .param("deviceY", "32.3636")
                            .param("pageSize", pageSize))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("페이지 크기는 양수만 가능합니다.")));
        }
    }

    @Nested
    class 리뷰_작성_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 사진이_포함된_경우_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            given(reviewService.create(any(ReviewCreateRequest.class), anyList(), any(Member.class)))
                    .willReturn(Review.builder().content(reviewCreateRequest.reviewContent()).build());

            mockMvc.perform(multipart(HttpMethod.POST, "/v1/reviews")
                            .file(request)
                            .file(multipartFile1)
                            .file(multipartFile2)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 사진이_없는_경우_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(Review.builder().content(reviewCreateRequest.reviewContent()).build());

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 사진이_4개_보다_많은_경우_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile3 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile4 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile5 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");

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
                    .andExpect(jsonPath("$.message").value(containsString("사진의 개수는 최대 4개까지 가능합니다.")));
        }

        @Test
        void 이미지_크기가_서블릿_최대_처리_크기_보다_큰_경우_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateRequest();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            MockMultipartFile multipartFile = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            given(reviewService.create(any(ReviewCreateRequest.class), anyList(), any(Member.class)))
                    .willThrow(new MaxUploadSizeExceededException(5));

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .file(multipartFile)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message").value(containsString("이미지의 크기는 최대 5MB 까지 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_위치_ID가_없는_경우_400_응답을_반환한다(String locationId) throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("장소 ID는 반드시 포함되어야 합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_이름이_없는_경우_400_응답을_반환한다(String storeName) throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 이름은 반드시 포함되어야 합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_주소가_없는_경우_400_응답을_반환한다(String address) throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 주소는 반드시 포함되어야 합니다.")));
        }

        @Test
        void 가게_경도가_없는_경우_400_응답을_반환한다() throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 경도는 반드시 포함되어야 합니다.")));
        }

        @Test
        void 가게_위도가_없는_경우_400_응답을_반환한다() throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 위도는 반드시 포함되어야 합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_정보_URL이_없는_경우_400_응답을_반환한다(String storeUrl) throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("카카오 서버의 가게 정보 url 반드시 포함되어야 합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_카테고리_정보가_없는_경우_400_응답을_반환한다(String category) throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 카테고리는 반드시 포함되어야 합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {" "})
        @NullAndEmptySource
        void 가게_리뷰_내용이_없는_경우_400_응답을_반환한다(String content) throws Exception {
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
                    null,
                    null
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 리뷰는 반드시 포함되어야 합니다.")));
        }
    }

    @Nested
    class 리뷰_수정_시 {
        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 이미지_변경_없이_정상적으로_수정되면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("리뷰 수정 내용",
                    Collections.emptyList());
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );
            willDoNothing().given(reviewService)
                    .update(anyLong(), any(ReviewUpdateRequest.class), anyList(), any(Member.class));

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void 이미지를_변경하고_정상적으로_수정되면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("리뷰 수정 내용",
                    Collections.emptyList());
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );
            willDoNothing().given(reviewService)
                    .update(anyLong(), any(ReviewUpdateRequest.class), anyList(), any(Member.class));

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void ID가_양수가_아니면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("리뷰 수정 내용",
                    Collections.emptyList()
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", -1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("리뷰 ID는 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = " ")
        void 수정하는_내용이_없으면_400_응답을_반환한다(String content) throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest(content,
                    Collections.emptyList()
            );
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("수정할 리뷰 내용은 반드시 포함되어야 합니다.")));
        }

        @Test
        void 삭제하는_사진_필드가_없으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("맛있어요", null);
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("삭제하는 사진 배열은 반드시 포함되어야 합니다.")));
        }

        @Test
        void 삭제하는_사진_ID가_음수이면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("맛있어요", List.of(-1L, -2L));
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("삭제하는 사진 ID는 양수만 가능합니다.")));
        }

        @Test
        void 삭제하는_사진_ID가_4개를_초과하면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("맛있어요", List.of(1L, 2L, 3L, 4L, 5L));
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
                            .file(request)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("삭제하는 사진은 최대 4개까지 가능합니다.")));
        }

        @Test
        void 추가_사진이_4개_보다_많은_경우_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewUpdateRequest reviewUpdateRequest = generateReviewUpdateRequest("리뷰", Collections.emptyList());
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewUpdateRequest)
            );
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile3 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile4 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");
            MockMultipartFile multipartFile5 = (MockMultipartFile) FileTestUtils.generateMultiPartFile("images");

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/reviews/{reviewId}", 1L)
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
                    .andExpect(jsonPath("$.message").value(containsString("사진의 개수는 최대 4개까지 가능합니다.")));
        }
    }

    @Nested
    class 리뷰_삭제_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적으로_삭제되면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(reviewService).delete(anyLong(), any(Member.class));

            mockMvc.perform(delete("/v1/reviews/{reviewId}", 1L)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 리뷰_ID가_Long_타입이_아니라면_400_응답을_반환한다(String reviewId) throws Exception {
            mockMvc.perform(delete("/v1/reviews/{reviewId}", reviewId)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-103"))
                    .andExpect(jsonPath("$.message",
                            containsString(Long.class.getSimpleName() + " 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 리뷰_ID가_양수가_아니면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(delete("/v1/reviews/{reviewId}", -1L)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("리뷰 ID는 양수만 가능합니다.")));
        }
    }

    private ReviewCreateRequest generateReviewCreateRequest() {
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
                null,
                null
        );
    }

    private ReviewUpdateRequest generateReviewUpdateRequest(
            String content,
            List<Long> deletePhotoIds
    ) {
        return new ReviewUpdateRequest(
                content,
                deletePhotoIds
        );
    }
}
