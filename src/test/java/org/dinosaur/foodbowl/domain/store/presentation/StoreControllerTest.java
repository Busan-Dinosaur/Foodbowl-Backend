package org.dinosaur.foodbowl.domain.store.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import org.dinosaur.foodbowl.domain.store.application.SchoolService;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoryResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
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

    @MockBean
    private SchoolService schoolService;

    @Nested
    class 가게_검색_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, ROLE_회원);

        @Test
        void 검색_키워드를_포함하는_검색_결과와_200_응답을_반환한다() throws Exception {
            StoreSearchResponses response = StoreSearchResponses.from(
                    List.of(new StoreSearchResponse(1L, "김밥나라", 1250, 10))
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

    @Test
    void 학교_목록_조회시_학교_목록과_200_응답을_반환한다() throws Exception {
        SchoolsResponse schoolsResponse = new SchoolsResponse(
                List.of(
                        new SchoolResponse(
                                1L,
                                "강남대학교",
                                "경기도 용인시 기흥구 강남로 40",
                                BigDecimal.valueOf(127.125),
                                BigDecimal.valueOf(35.12)
                        ),
                        new SchoolResponse(
                                2L,
                                "부산대학교",
                                "부산광역시 금정구 부산대학로63번길 2",
                                BigDecimal.valueOf(127.350),
                                BigDecimal.valueOf(35.78)
                        )
                )
        );
        given(schoolService.getSchools()).willReturn(schoolsResponse);

        MvcResult mvcResult = mockMvc.perform(get("/v1/stores/schools")
                        .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        SchoolsResponse result = objectMapper.readValue(jsonResponse, SchoolsResponse.class);

        assertThat(result).usingRecursiveComparison().isEqualTo(schoolsResponse);
    }
}
