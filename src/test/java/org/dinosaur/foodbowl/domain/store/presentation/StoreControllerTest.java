package org.dinosaur.foodbowl.domain.store.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoryResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Test;
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
}
