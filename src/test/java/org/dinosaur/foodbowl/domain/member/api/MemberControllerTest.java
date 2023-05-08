package org.dinosaur.foodbowl.domain.member.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.response.DuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.dto.request.DuplicationCheckRequest;
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
        private final DuplicationCheckRequest request = new DuplicationCheckRequest("gray");

        @Test
        @DisplayName("요청 닉네임이 존재하면 true를 반환한다.")
        void checkDuplicateTrue() throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(true));

            mockMvc.perform(post("/api/v1/members/check-nicknames")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasDuplicate").value(true))
                    .andDo(print());
        }

        @Test
        @DisplayName("요청 닉네임이 존재하지 않으면 false를 반환한다.")
        void checkDuplicateFalse() throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(false));

            mockMvc.perform(post("/api/v1/members/check-nicknames")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.hasDuplicate").value(false))
                    .andDo(print());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "gray1234", "qwertyasdfg"})
        @DisplayName("닉네임이 1자 이상, 10자 이하의 한글 또는 영문이 아니면 BAD REQUEST가 발생한다.")
        void checkDuplicateFail(String nickname) throws Exception {
            given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(false));

            mockMvc.perform(post("/api/v1/members/check-nicknames")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new DuplicationCheckRequest(nickname)))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }
}
