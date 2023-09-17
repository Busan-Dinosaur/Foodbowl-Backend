package org.dinosaur.foodbowl.domain.member.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(MemberController.class)
class MemberControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private MemberService memberService;

    @Nested
    class 프로필_조회_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 프로필을_조회에_성공하면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            MemberProfileResponse response = new MemberProfileResponse(
                    1L,
                    "https://justdoeat.shop/static/images/thumbnail/profile/image.png",
                    "coby5502",
                    "반갑습니다!",
                    100,
                    1000,
                    true,
                    false
            );
            given(memberService.getProfile(anyLong(), any(Member.class))).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/members/{memberId}/profile", 1L)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            MemberProfileResponse result = objectMapper.readValue(jsonResponse, MemberProfileResponse.class);

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 멤버_ID가_Long_타입이_아니라면_400_응답을_반환한다(String memberId) throws Exception {
            mockMvc.perform(get("/v1/members/{memberId}/profile", memberId)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-103"))
                    .andExpect(jsonPath("$.message",
                            containsString(Long.class.getSimpleName() + " 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 멤버_ID가_양수가_아니라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/members/{memberId}/profile", -1L)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("ID는 양수만 가능합니다.")));
        }
    }

    @Test
    void 내_프로필_조회에_성공하면_200_응답을_반환한다() throws Exception {
        mockingAuthMemberInResolver();
        MemberProfileResponse response = new MemberProfileResponse(
                1L,
                "https://justdoeat.shop/static/images/thumbnail/profile/image.png",
                "coby5502",
                "반갑습니다!",
                100,
                1000,
                true,
                false
        );
        given(memberService.getMyProfile(any(Member.class))).willReturn(response);

        MvcResult mvcResult = mockMvc.perform(get("/v1/members/me/profile")
                        .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        MemberProfileResponse result = objectMapper.readValue(jsonResponse, MemberProfileResponse.class);

        assertThat(result).usingRecursiveComparison().isEqualTo(response);
    }

    @Nested
    class 닉네임_존재_여부_확인_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 닉네임_존재_여부_확인을_성공하면_200_응답을_반환한다() throws Exception {
            NicknameExistResponse response = new NicknameExistResponse(true);
            given(memberService.checkNicknameExist(anyString())).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/members/nickname/exist")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("nickname", "hello"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            NicknameExistResponse result = objectMapper.readValue(jsonResponse, NicknameExistResponse.class);

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @NullSource
        void 닉네임_파라미터가_존재하지_않으면_400_응답을_반환한다(String nickname) throws Exception {
            mockMvc.perform(get("/v1/members/nickname/exist")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("nickname", nickname))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-104"))
                    .andExpect(jsonPath("$.message", containsString("파라미터가 필요합니다.")));
        }

        @ParameterizedTest
        @EmptySource
        void 닉네임이_공백이라면_400_응답을_반환한다(String nickname) throws Exception {
            mockMvc.perform(get("/v1/members/nickname/exist")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("nickname", nickname))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("닉네임 파라미터 값이 존재하지 않습니다.")));
        }
    }

    @Nested
    class 프로필_정보_수정_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 프로필_정보_수정에_성공하면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            UpdateProfileRequest request = new UpdateProfileRequest("coby5502", "동네 맛집 탐험을 좋아하는 아저씨에요.");
            willDoNothing().given(memberService).updateProfile(any(UpdateProfileRequest.class), any(Member.class));

            mockMvc.perform(patch("/v1/members/profile")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void 닉네임이_공백이거나_존재하지_않으면_400_응답을_반환한다(String nickname) throws Exception {
            UpdateProfileRequest request = new UpdateProfileRequest(nickname, "동네 맛집 탐험을 좋아하는 아저씨에요.");

            mockMvc.perform(patch("/v1/members/profile")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("닉네임이 공백이거나 존재하지 않습니다.")));
        }
    }
}
