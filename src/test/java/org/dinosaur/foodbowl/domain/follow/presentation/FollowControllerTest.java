package org.dinosaur.foodbowl.domain.follow.presentation;

import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.follow.application.FollowService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
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
@WebMvcTest(FollowController.class)
class FollowControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private FollowService followService;

    @Nested
    class 팔로우 {

        @ValueSource(strings = {"가", "a", "A", "@"})
        @ParameterizedTest
        void ID타입으로_변환하지_못하는_타입이라면_400_응답을_반환한다(String memberId) throws Exception {
            mockMvc.perform(post("/v1/follows/{memberId}/follow", memberId)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message",
                            containsString(Long.class.getSimpleName() + " 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void ID가_양수가_아니라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(post("/v1/follows/{memberId}/follow", -1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("ID는 양수만 가능합니다.")));
        }

        @Test
        void 팔로우를_수행하면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(followService)
                    .follow(anyLong(), any(Member.class));

            mockMvc.perform(post("/v1/follows/{memberId}/follow", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class 언팔로우 {

        @ValueSource(strings = {"가", "a", "A", "@"})
        @ParameterizedTest
        void ID타입으로_변환하지_못하는_타입이라면_400_응답을_반환한다(String memberId) throws Exception {
            mockMvc.perform(delete("/v1/follows/{memberId}/unfollow", memberId)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message",
                            containsString(Long.class.getSimpleName() + " 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void ID가_양수가_아니라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(delete("/v1/follows/{memberId}/unfollow", -1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("ID는 양수만 가능합니다.")));
        }

        @Test
        void 언팔로우를_수행하면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(followService)
                    .unfollow(anyLong(), any(Member.class));

            mockMvc.perform(delete("/v1/follows/{memberId}/unfollow", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class 팔로워_삭제 {

        @ValueSource(strings = {"가", "a", "A", "@"})
        @ParameterizedTest
        void ID타입으로_변환하지_못하는_타입이라면_400_응답을_반환한다(String memberId) throws Exception {
            mockMvc.perform(delete("/v1/follows/followers/{memberId}", memberId)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message").value("요청 데이터 타입이 일치하지 않습니다."));
        }

        @Test
        void 팔로워_삭제를_수행하면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            willDoNothing().given(followService)
                    .follow(anyLong(), any(Member.class));

            mockMvc.perform(delete("/v1/follows/followers/{memberId}", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }
}
