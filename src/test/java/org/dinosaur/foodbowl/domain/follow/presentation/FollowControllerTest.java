package org.dinosaur.foodbowl.domain.follow.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dinosaur.foodbowl.domain.member.domain.vo.RoleType.ROLE_회원;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.follow.application.FollowService;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowingResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowerResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.common.response.PageResponse;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(FollowController.class)
class FollowControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private FollowService followService;

    @Nested
    class 팔로잉_목록_조회 {

        @Test
        void 팔로잉_목록을_조회하면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            PageResponse<FollowingResponse> response = new PageResponse<>(
                    List.of(new FollowingResponse(
                            1L,
                            "http://justdoeat.shop/static/images/profile.png",
                            "coby5502",
                            20
                    )),
                    true,
                    true,
                    false,
                    0,
                    1
            );
            given(followService.getFollowings(anyInt(), anyInt(), any(Member.class))).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/follows/followings")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString();
            PageResponse<FollowingResponse> result = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<PageResponse<FollowingResponse>>() {
                    }
            );

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 페이지가_INT_타입으로_변환하지_못하면_400_응답을_반환한다(String page) throws Exception {
            mockMvc.perform(get("/v1/follows/followings")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", page)
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message", containsString("int 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 페이지가_음수라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/followings")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "-1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("페이지는 0이상만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 페이지_크기가_INT_타입으로_변환하지_못하면_400_응답을_반환한다(String size) throws Exception {
            mockMvc.perform(get("/v1/follows/followings")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", size))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message", containsString("int 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 페이지_크기가_음수라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/followings")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "-1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("페이지 크기는 0이상만 가능합니다.")));
        }
    }

    @Nested
    class 팔로워_목록_조회 {

        @Test
        void 팔로워_목록을_조회하면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            PageResponse<FollowerResponse> response = new PageResponse<>(
                    List.of(new FollowerResponse(
                            1L,
                            "http://justdoeat.shop/static/images/profile.png",
                            "coby5502",
                            20
                    )),
                    true,
                    true,
                    false,
                    0,
                    1
            );
            given(followService.getFollowers(anyInt(), anyInt(), any(Member.class))).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/follows/followers")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString();
            PageResponse<FollowerResponse> result = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<PageResponse<FollowerResponse>>() {
                    }
            );

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 페이지가_INT_타입으로_변환하지_못하면_400_응답을_반환한다(String page) throws Exception {
            mockMvc.perform(get("/v1/follows/followers")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", page)
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message", containsString("int 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 페이지가_음수라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/followers")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "-1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("페이지는 0이상만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 페이지_크기가_INT_타입으로_변환하지_못하면_400_응답을_반환한다(String size) throws Exception {
            mockMvc.perform(get("/v1/follows/followers")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", size))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message", containsString("int 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 페이지_크기가_음수라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/followers")
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "-1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("페이지 크기는 0이상만 가능합니다.")));
        }
    }

    @Nested
    class 다른_회원_팔로워_목록_조회 {

        @Test
        void 팔로워_목록을_조회하면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            PageResponse<OtherUserFollowerResponse> response = new PageResponse<>(
                    List.of(new OtherUserFollowerResponse(
                            1L,
                            "http://justdoeat.shop/static/images/profile.png",
                            "coby5502",
                            20,
                            true
                    )),
                    true,
                    true,
                    false,
                    0,
                    1
            );
            given(followService.getOtherUserFollowers(anyLong(), anyInt(), anyInt(), any(Member.class)))
                    .willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/v1/follows/{memberId}/followers", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonResponse = mvcResult.getResponse().getContentAsString();
            PageResponse<OtherUserFollowerResponse> result = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<PageResponse<OtherUserFollowerResponse>>() {
                    }
            );

            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void ID가_Long타입으로_변환하지_못하면_400_응답을_반환한다(String memberId) throws Exception {
            mockMvc.perform(get("/v1/follows/{memberId}/followers", memberId)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message",
                            containsString(Long.class.getSimpleName() + " 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void ID가_양수가_아니라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/{memberId}/followers", -1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("ID는 양수만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 페이지가_INT_타입으로_변환하지_못하면_400_응답을_반환한다(String page) throws Exception {
            mockMvc.perform(get("/v1/follows/{memberId}/followers", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", page)
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message", containsString("int 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 페이지가_음수라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/{memberId}/followers", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "-1")
                            .param("size", "1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("페이지는 0이상만 가능합니다.")));
        }

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void 페이지_크기가_INT_타입으로_변환하지_못하면_400_응답을_반환한다(String size) throws Exception {
            mockMvc.perform(get("/v1/follows/{memberId}/followers", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", size))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-102"))
                    .andExpect(jsonPath("$.message", containsString("int 타입으로 변환할 수 없는 요청입니다.")));
        }

        @Test
        void 페이지_크기가_음수라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();

            mockMvc.perform(get("/v1/follows/{memberId}/followers", 1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원))
                            .param("page", "1")
                            .param("size", "-1"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("페이지 크기는 0이상만 가능합니다.")));
        }
    }

    @Nested
    class 팔로우 {

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

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void ID가_LONG_타입으로_변환하지_못하면_400_응답을_반환한다(String memberId) throws Exception {
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
    }

    @Nested
    class 언팔로우 {

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

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void ID가_Long타입으로_변환하지_못하면_400_응답을_반환한다(String memberId) throws Exception {
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
    }

    @Nested
    class 팔로워_삭제 {

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

        @ParameterizedTest
        @ValueSource(strings = {"가", "a", "A", "@"})
        void ID가_Long타입으로_변환하지_못하면_400_응답을_반환한다(String memberId) throws Exception {
            mockMvc.perform(delete("/v1/follows/followers/{memberId}", memberId)
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

            mockMvc.perform(delete("/v1/follows/followers/{memberId}", -1L)
                            .header(AUTHORIZATION, BEARER + jwtTokenProvider.createAccessToken(1L, ROLE_회원)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-101"))
                    .andExpect(jsonPath("$.message", containsString("ID는 양수만 가능합니다.")));
        }
    }
}
