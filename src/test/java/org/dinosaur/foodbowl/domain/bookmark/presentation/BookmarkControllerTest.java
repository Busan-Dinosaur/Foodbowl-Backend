package org.dinosaur.foodbowl.domain.bookmark.presentation;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private BookmarkService bookmarkService;

    @Nested
    class 북마크_추가_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 정상적으로_추가되면_200_상태코드를_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(bookmarkService).save(anyLong(), any(Member.class));

            mockMvc.perform(post("/v1/bookmarks")
                            .param("storeId", "1")
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @ParameterizedTest
        @ValueSource(strings = {"hello", "@#!"})
        void 가게_ID가_숫자가_아니면_400_상태코드를_반환한다(String storeId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(post("/v1/bookmarks")
                            .param("storeId", storeId)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message").value(containsString("Long 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 가게_ID가_음수면_400_상태코드를_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(post("/v1/bookmarks")
                            .param("storeId", "-1")
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 ID는 양수만 가능합니다.")));
        }
    }

    @Nested
    class 북마크_삭제_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 정상적으로_삭제되면_204_상태코드를_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(bookmarkService).delete(anyLong(), any(Member.class));

            mockMvc.perform(delete("/v1/bookmarks")
                            .param("storeId", "1")
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @ValueSource(strings = {"hello", "@#!"})
        void 가게_ID가_숫자가_아니면_400_상태코드를_반환한다(String storeId) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(delete("/v1/bookmarks")
                            .param("storeId", storeId)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message").value(containsString("Long 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 가게_ID가_음수면_400_상태코드를_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(delete("/v1/bookmarks")
                            .param("storeId", "-1")
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message").value(containsString("가게 ID는 양수만 가능합니다.")));
        }
    }
}
