package org.dinosaur.foodbowl.domain.member.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberSearchResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberSearchResponses;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.dinosaur.foodbowl.test.file.FileTestUtils;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

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
    class 닉네임으로_회원_검색_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 정상적으로_검색하면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            MemberSearchResponses response = new MemberSearchResponses(
                    List.of(new MemberSearchResponse(1L, "gray", "https://image.com", 10, true, false))
            );
            given(memberService.search(anyString(), anyInt(), any(Member.class)))
                    .willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/members/search")
                            .param("name", "gray")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            MemberSearchResponses result = objectMapper.readValue(jsonResponse, MemberSearchResponses.class);

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", ""})
        void 검색어가_없거나_공백이면_400_응답을_반환한다(String name) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/members/search")
                            .param("name", name)
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("message").value(containsString("검색어는 빈 값이 될 수 없습니다.")));
        }

        @Test
        void 검색어_파라미터가_없으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/members/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @ValueSource(ints = {51, 100})
        void 결과_응답_수가_최대_응답_수보다_크면_400_응답을_반환한다(int size) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/members/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "gray")
                            .param("size", String.valueOf(size))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("message").value(containsString("최대 50개까지 조회가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -10})
        void 결과_응답_수가_0_이하이면_400_응답을_반환한다(int size) throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/members/search")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .param("name", "gray")
                            .param("size", String.valueOf(size))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("message").value(containsString("조회 크기는 1이상만 가능합니다.")));
        }
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

    @Nested
    class 프로필_이미지_수정_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 프로필_이미지_수정에_성공하면_204_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            MockMultipartFile file = (MockMultipartFile) FileTestUtils.generateMultiPartFile("image");
            willDoNothing().given(memberService).updateProfileImage(any(MultipartFile.class), any(Member.class));

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/members/profile/image")
                            .file(file)
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }

        @Test
        void 프로필_이미지가_존재하지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(multipart(HttpMethod.PATCH, "/v1/members/profile/image")
                            .header(AUTHORIZATION, BEARER + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-106"))
                    .andExpect(jsonPath("$.message", containsString("파트가 필요합니다.")));
        }
    }

    @Test
    void 프로필_이미지_삭제에_성공하면_204_응답을_반환한다() throws Exception {
        mockingAuthMemberInResolver();
        willDoNothing().given(memberService).deleteProfileImage(any(Member.class));
        String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        mockMvc.perform(delete("/v1/members/profile/image")
                        .header(AUTHORIZATION, BEARER + accessToken))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
