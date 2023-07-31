package org.dinosaur.foodbowl.domain.healthcheck.presentation;

import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import org.dinosaur.foodbowl.PresentationTest;
import org.dinosaur.foodbowl.domain.auth.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.healthcheck.application.HealthCheckService;
import org.dinosaur.foodbowl.domain.healthcheck.dto.response.HealthCheckResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.SocialType;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(controllers = HealthCheckControllerImpl.class)
class HealthCheckControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private HealthCheckService healthCheckService;

    @Test
    void 서버_상태가_정상이면_200_반환() throws Exception {
        given(healthCheckService.healthCheck())
                .willReturn(new HealthCheckResponse("good"));

        mockMvc.perform(get("/v1/health-check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("good"))
                .andDo(print());
    }

    @Test
    void 사용자_인증이_정상이면_200_반환() throws Exception {
        Member member = Member.builder()
                .email("foodBowl@gmail.com")
                .socialId("foodBowlId")
                .socialType(SocialType.APPLE)
                .nickname("foodbowl")
                .introduction("푸드볼 서비스")
                .build();
        given(memberRepository.findById(anyLong()))
                .willReturn(Optional.of(member));
        given(healthCheckService.healthCheck())
                .willReturn(new HealthCheckResponse("good"));

        mockMvc.perform(get("/v1/health-check/auth")
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("good"))
                .andDo(print());
    }
}
