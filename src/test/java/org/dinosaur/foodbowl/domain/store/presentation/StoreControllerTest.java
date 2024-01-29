package org.dinosaur.foodbowl.domain.store.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.review.dto.request.MapCoordinateRequest;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoryResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponses;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponses;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(StoreController.class)
class StoreControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private StoreService storeService;

    @Test
    void 가게_카테고리_목록_조회시_카테고리_목록과_200_응답을_반환한다() throws Exception {
        CategoriesResponse categoriesResponse = new CategoriesResponse(
                List.of(
                        new CategoryResponse(1L, "카페"),
                        new CategoryResponse(2L, "술집"))
        );
        given(storeService.getCategories()).willReturn(categoriesResponse);

        MvcResult mvcResult = mockMvc.perform(get("/v1/stores/categories")
                        .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        CategoriesResponse result = objectMapper.readValue(jsonResponse, CategoriesResponse.class);

        assertThat(result).usingRecursiveComparison().isEqualTo(categoriesResponse);
    }

    @Nested
    class 가게_검색_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 검색_키워드를_포함하는_검색_결과와_200_응답을_반환한다() throws Exception {
            StoreSearchResponses response = StoreSearchResponses.from(
                    List.of(new StoreSearchResponse(
                            1L,
                            "김밥나라",
                            "서울시 강남구 강남로",
                            "한식",
                            1250,
                            10)
                    )
            );
            given(storeService.search(anyString(), any(BigDecimal.class), any(BigDecimal.class), anyInt()))
                    .willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("x", "127.3412")
                            .param("y", "37.2341")
                            .param("size", "15")
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreSearchResponses result = objectMapper.readValue(jsonResponse,
                    StoreSearchResponses.class);

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", ""})
        void 검색어가_빈_값이나_공백이면_400_응답을_반환한다(String name) throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", name)
                            .param("x", "127.3412")
                            .param("y", "37.2341")
                            .param("size", "15")
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("message").value(containsString("검색어는 빈 값이 될 수 없습니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"hello", "!@#$%", "java1234"})
        void 사용자의_경도_값이_숫자가_아니면_400_응답을_반환한다(String x) throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("x", x)
                            .param("y", "37.2341")
                            .param("size", "15")
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-103"))
                    .andExpect(jsonPath("message").value(containsString("BigDecimal 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 사용자의_경도_값이_없으면_400_응답을_반환한다() throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("y", "37.2341")
                            .param("size", "15")
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"hello", "!@#$%", "java1234"})
        void 사용자의_위도_값이_숫자가_아니면_400_응답을_반환한다(String y) throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("x", "126.1256")
                            .param("y", y)
                            .param("size", "15")
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-103"))
                    .andExpect(jsonPath("message").value(containsString("BigDecimal 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 사용자의_위도_값이_없으면_400_응답을_반환한다() throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("x", "127.2341")
                            .param("size", "15")
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(ints = {31, 50})
        void 결과_응답_수가_최대_응답_수보다_크면_400_응답을_반환한다(int size) throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("x", "126.1256")
                            .param("y", "36.1255")
                            .param("size", String.valueOf(size))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("message").value(containsString("최대 30개까지 조회가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        void 결과_응답_수가_0_이하이면_400_응답을_반환한다(int size) throws Exception {
            mockMvc.perform(get("/v1/stores/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "김밥")
                            .param("x", "126.1256")
                            .param("y", "36.1255")
                            .param("size", String.valueOf(size))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("message").value(containsString("조회 크기는 1이상만 가능합니다.")));
        }
    }

    @Nested
    class 멤버의_리뷰가_존재하는_가게_목록_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            StoreMapBoundResponses response = mockStoreMapBoundResponses();
            given(storeService.getStoresByMemberInMapBounds(
                    anyLong(),
                    any(MapCoordinateRequest.class),
                    any(Member.class))
            ).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.1212")
                            .param("deltaY", "3.1212"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreMapBoundResponses result = objectMapper.readValue(jsonResponse, StoreMapBoundResponses.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 멤버ID가_양수가_아니라면_400_응답을_반환한다(String memberId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", memberId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.1212")
                            .param("deltaY", "3.1212"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("멤버 ID는 양수만 가능합니다.")));
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/members")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("memberId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }
    }

    @Nested
    class 북마크한_가게_목록_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            StoreMapBoundResponses response = mockStoreMapBoundResponses();
            given(storeService.getStoresByBookmarkInMapBounds(any(MapCoordinateRequest.class), any(Member.class)))
                    .willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.1212")
                            .param("deltaY", "3.1212"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreMapBoundResponses result = objectMapper.readValue(jsonResponse, StoreMapBoundResponses.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/bookmarks")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }
    }

    @Nested
    class 팔로잉_하는_유저의_리뷰가_존재하는_가게_목록_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            StoreMapBoundResponses response = mockStoreMapBoundResponses();
            given(storeService.getStoresByFollowingInMapBounds(any(MapCoordinateRequest.class), any(Member.class)))
                    .willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.1212")
                            .param("deltaY", "3.1212"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreMapBoundResponses result = objectMapper.readValue(jsonResponse, StoreMapBoundResponses.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/followings")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }
    }

    @Nested
    class 학교_근거_가게_목록_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            StoreMapBoundResponses response = mockStoreMapBoundResponses();
            given(storeService.getStoresBySchoolInMapBounds(
                    anyLong(),
                    any(MapCoordinateRequest.class),
                    any(Member.class))
            ).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.1212")
                            .param("deltaY", "3.1212"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            StoreMapBoundResponses result = objectMapper.readValue(jsonResponse, StoreMapBoundResponses.class);
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 학교ID가_양수가_아니라면_400_응답을_반환한다(String schoolId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", schoolId)
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.1212")
                            .param("deltaY", "3.1212"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("학교 ID는 양수만 가능합니다.")));
        }

        @Test
        void 경도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 경도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 위도_증가값이_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 경도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaX) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", deltaX)
                            .param("deltaY", "3.12"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("경도 증가값은 0이상의 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "0"})
        void 위도_증가값이_0이상의_양수가_아니라면_400_응답을_반환한다(String deltaY) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/stores/schools")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("schoolId", "1")
                            .param("x", "123.3636")
                            .param("y", "32.3636")
                            .param("deltaX", "3.12")
                            .param("deltaY", deltaY))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("위도 증가값은 0이상의 양수만 가능합니다.")));
        }
    }

    private StoreMapBoundResponses mockStoreMapBoundResponses() {
        return new StoreMapBoundResponses(
                List.of(
                        new StoreMapBoundResponse(
                                1L,
                                "가게 이름",
                                "가게 카테고리명",
                                "가게 주소",
                                "가게 URL",
                                123.3636,
                                32.3636,
                                5,
                                false
                        )
                )
        );
    }
}
