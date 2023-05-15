package org.dinosaur.foodbowl.domain.member.api;

import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends MockApiTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원 탈퇴 요청 시 회원을 탈퇴시킨다.")
    void withDraw() throws Exception {
        willDoNothing().given(memberService).withDraw(anyLong());

        mockMvc.perform(delete("/api/v1/members")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
