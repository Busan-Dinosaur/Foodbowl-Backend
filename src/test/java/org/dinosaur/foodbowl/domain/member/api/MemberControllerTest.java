package org.dinosaur.foodbowl.domain.member.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.request.ProfileUpdateRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(controllers = MemberController.class)
class MemberControllerTest extends MockApiTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Nested
    @DisplayName("회원 프로필 정보 조회 요청 시 ")
    class GetMemberProfile {

        @ParameterizedTest
        @ValueSource(strings = {"a", "가", "@"})
        @DisplayName("정수로 변환할 수 없는 타입의 ID라면 400 상태를 반환한다.")
        void return400WhenFailToConvertToLong(String id) throws Exception {
            mockMvc.perform(get("/api/v1/members/{id}/profile", id)
                            .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("타입이 잘못되었습니다.")))
                    .andExpect(jsonPath("$.code").value(-1002));
        }

        @Test
        @DisplayName("요청에 성공한다면 200 상태를 반환한다.")
        void return200WhenRequestSuccess() throws Exception {
            String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
            MemberProfileResponse response = new MemberProfileResponse(
                    "dazzle",
                    "http://foodbowl/thumbnail/1",
                    176,
                    124,
                    false,
                    true
            );

            given(memberService.getMemberProfile(anyLong(), anyLong())).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/members/{id}/profile", 2L)
                            .header("Authorization", "Bearer " + accessToken)
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            MemberProfileResponse result = objectMapper.readValue(jsonResponse, MemberProfileResponse.class);

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }
    }

    @Nested
    @DisplayName("닉네임, 소개 수정 요청 시 ")
    class updateProfile {

        private String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("인증되지 않은 회원이라면 401 상태를 반환한다.")
        void updateProfileWithUnAuthenticated() throws Exception {
            ProfileUpdateRequest request = new ProfileUpdateRequest("foodbowl", "Foodbowl is Good");

            mockMvc.perform(put("/api/v1/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("닉네임 정보가 존재하지 않으면 400 상태를 반환한다.")
        void updateProfileWithNotExistNickname(String nickname) throws Exception {
            ProfileUpdateRequest request = new ProfileUpdateRequest(nickname, "Foodbowl is Good");

            mockMvc.perform(put("/api/v1/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("닉네임은 존재해야 합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "graygraygraygrayhoy", "@!!dsafdsf$"})
        @DisplayName("유효하지 않은 닉네임이라면 400 상태를 반환한다.")
        void updateProfileWithInvalidNickname(String nickname) throws Exception {
            ProfileUpdateRequest request = new ProfileUpdateRequest(nickname, "Foodbowl is Good");

            mockMvc.perform(put("/api/v1/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("닉네임은 1자 이상 16자 이하 한글, 영문, 숫자만 가능합니다")));
        }

        @Test
        @DisplayName("소개가 255자를 넘는다면 400 상태를 반환한다.")
        void updateProfileWithInvalidIntroduction() throws Exception {
            ProfileUpdateRequest request = new ProfileUpdateRequest("foodbowl", "a".repeat(256));

            mockMvc.perform(put("/api/v1/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(containsString("소개는 최대 255자까지만 가능합니다.")));
        }

        @Test
        @DisplayName("유효한 정보라면 204 상태를 반환한다.")
        void updateProfile() throws Exception {
            ProfileUpdateRequest request = new ProfileUpdateRequest("foodbowl", "Foodbowl is Good");

            willDoNothing().given(memberService).updateProfile(anyLong(), any(ProfileUpdateRequest.class));

            mockMvc.perform(put("/api/v1/members")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
    }
}
