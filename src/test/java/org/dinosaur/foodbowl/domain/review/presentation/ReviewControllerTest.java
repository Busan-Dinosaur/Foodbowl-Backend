package org.dinosaur.foodbowl.domain.review.presentation;

import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.application.ReviewService;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
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
    class 리뷰_작성_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 사진이_포함된_경우_200_상태코드를_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
            given(reviewService.create(any(ReviewCreateRequest.class), anyList(), any(Member.class)))
                    .willReturn(1L);

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
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            given(reviewService.create(any(ReviewCreateRequest.class), any(), any(Member.class)))
                    .willReturn(1L);

            mockMvc.perform(multipart("/v1/reviews")
                            .file(request)
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 사진이_4개_보다_많은_경우_400_상태코드를_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            MockMultipartFile multipartFile1 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
            MockMultipartFile multipartFile2 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
            MockMultipartFile multipartFile3 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
            MockMultipartFile multipartFile4 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
            MockMultipartFile multipartFile5 = (MockMultipartFile) FileTestUtils.generateMultiPartFile();

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
        void 이미지_크기가_서블릿_최대_처리_크기_보다_큰_경우_400_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            ReviewCreateRequest reviewCreateRequest = generateReviewCreateDto();
            MockMultipartFile request = new MockMultipartFile(
                    "request",
                    "",
                    "application/json",
                    objectMapper.writeValueAsBytes(reviewCreateRequest)
            );
            MockMultipartFile multipartFile = (MockMultipartFile) FileTestUtils.generateMultiPartFile();
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
        void 가게_위치_ID가_없는_경우_400_상태코드를_반환한다(String locationId) throws Exception {
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
        void 가게_이름이_없는_경우_400_상태코드를_반환한다(String storeName) throws Exception {
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
        void 가게_주소가_없는_경우_400_상태코드를_반환한다(String address) throws Exception {
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
        void 가게_경도가_없는_경우_400_상태코드를_반환한다() throws Exception {
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
        void 가게_위도가_없는_경우_400_상태코드를_반환한다() throws Exception {
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
        void 가게_정보_URL_없는_경우_400_상태코드를_반환한다(String storeUrl) throws Exception {
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
        void 가게_카테고리_정보가_없는_경우_400_상태코드를_반환한다(String category) throws Exception {
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
        void 가게_리뷰_내용이_없는_경우_400_상태코드를_반환한다(String content) throws Exception {
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
    class 리뷰_삭제_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적으로_삭제되면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(reviewService).delete(anyLong(), any(Member.class));

            mockMvc.perform(delete("/v1/reviews/{id}", 1L)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void ID를_Long_타입으로_변환하지_못하면_400_응답을_반환한다(String reviewId) throws Exception {
            mockMvc.perform(delete("/v1/reviews/{id}", reviewId)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message",
                            containsString(Long.class.getSimpleName() + " 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void ID가_양수가_아니면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(delete("/v1/reviews/{id}", -1L)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("ID는 양수만 가능합니다.")));
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
