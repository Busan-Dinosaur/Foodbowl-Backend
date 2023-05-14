package org.dinosaur.foodbowl.domain.member.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.response.DuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends MockApiTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("닉네임 중복 검증은")
    class CheckDuplicate {

        private final String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("요청 파라미터가 없으면 BAD REQUEST가 발생한다.")
        void checkDuplicateFailWithNoParams() throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(false));

            mockMvc.perform(get("/api/v1/members/check-nickname")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("요청 닉네임이 존재하면 true를 반환한다.")
        void checkDuplicateTrue() throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(true));

            mockMvc.perform(get("/api/v1/members/check-nickname")
                            .header("Authorization", "Bearer " + token)
                            .queryParam("nickname", "gray")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasDuplicate").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("요청 닉네임이 존재하지 않으면 false를 반환한다.")
        void checkDuplicateFalse() throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(false));

            mockMvc.perform(get("/api/v1/members/check-nickname")
                            .header("Authorization", "Bearer " + token)
                            .queryParam("nickname", "gray")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasDuplicate").value(false))
                    .andDo(print());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "graygraygraygrayhoy", "@!!dsafdsf$"})
        @DisplayName("닉네임은 1자 이상 16자 이하 한글,영문,숫자가 아니면 BAD REQUEST가 발생한다.")
        void checkDuplicateFail(String nickname) throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(false));

            mockMvc.perform(get("/api/v1/members/check-nickname")
                            .header("Authorization", "Bearer " + token)
                            .queryParam("nickname", nickname)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
