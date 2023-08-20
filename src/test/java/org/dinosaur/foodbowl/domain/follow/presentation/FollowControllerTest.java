package org.dinosaur.foodbowl.domain.follow.presentation;

import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
            mockMvc.perform(MockMvcRequestBuilders.post("/v1/follows/{memberId}/follow", memberId)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message").value("요청 데이터 타입이 일치하지 않습니다."));
        }

        @Test
        void 팔로우를_수행하면_200_응답을_반환한다() throws Exception {
            given(memberRepository.findById(anyLong()))
                    .willReturn(Optional.of(member));
            willDoNothing().given(followService)
                    .follow(anyLong(), any(Member.class));

            mockMvc.perform(MockMvcRequestBuilders.post("/v1/follows/{memberId}/follow", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}