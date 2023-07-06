package org.dinosaur.foodbowl.domain.post.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.domain.post.application.PostService;
import org.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequest;
import org.dinosaur.foodbowl.domain.post.dto.response.PostStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.testsupport.fixture.MultipartFileFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(PostController.class)
class PostControllerTest extends MockApiTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @Test
    @DisplayName("최근 게시글 썸네일 목록을 조회한다.")
    void findLatestThumbnails() throws Exception {
        PageResponse<PostThumbnailResponse> response = new PageResponse<>(
                List.of(
                        new PostThumbnailResponse(1L, "path", LocalDateTime.now()),
                        new PostThumbnailResponse(2L, "path", LocalDateTime.now())
                ),
                true,
                true,
                false,
                0,
                1,
                1,
                1
        );
        given(postService.findLatestThumbnails(any(Pageable.class))).willReturn(response);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/thumbnails/latest")
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
        PageResponse<PostThumbnailResponse> result =
                objectMapper.readValue(jsonResponse, new TypeReference<PageResponse<PostThumbnailResponse>>() {
                });
        assertThat(result).usingRecursiveComparison().isEqualTo(response);
    }

    @Nested
    @DisplayName("게시글 생성 시 ")
    class CreatePost {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("유효한 요청이라면 Created 와 redirectUrl을 응답한다.")
        void createPost_success() throws Exception {
            PostCreateRequest postCreateRequest = new PostCreateRequest(1L, List.of("카페", "한식"), "맛있다");

            given(postService.save(anyLong(), any(PostCreateRequest.class), anyList())).willReturn(1L);

            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(postCreateRequest));

            mockMvc.perform(multipart("/api/v1/posts")
                            .file(request)
                            .file(MultipartFileFixture.get())
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(redirectedUrl("/api/v1/posts/1"));
        }

        @Test
        @DisplayName("가게 ID를 전달하지 않으면 400 상태를 반환한다.")
        void createPost_fail_emptyStore() throws Exception {
            PostCreateRequest postCreateRequest = new PostCreateRequest(null, List.of("카페", "한식"), "맛있다");

            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(postCreateRequest));

            mockMvc.perform(multipart("/api/v1/posts")
                            .file(request)
                            .file(MultipartFileFixture.get())
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("가게 ID는 반드시 포함되어야 합니다.")));
        }

        @Test
        @DisplayName("카테고리 개수가 0이라면 400 상태를 반환한다.")
        void createPost_fail_zeroCategory() throws Exception {
            PostCreateRequest postCreateRequest = new PostCreateRequest(1L, Collections.emptyList(), "맛있다");

            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(postCreateRequest));

            mockMvc.perform(multipart("/api/v1/posts")
                            .file(request)
                            .file(MultipartFileFixture.get())
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("카테고리는 최소 1개, 최대 8개까지 선택 가능합니다.")));
        }

        @Test
        @DisplayName("사진이 제한 개수를 초과하면 400 상태를 반환한다.")
        void createPost_fail_overImageFiles() throws Exception {
            PostCreateRequest postCreateRequest = new PostCreateRequest(1L, List.of("카페", "한식"), "맛있다");

            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(postCreateRequest));

            mockMvc.perform(multipart("/api/v1/posts")
                            .file(request)
                            .file(MultipartFileFixture.get()).file(MultipartFileFixture.get()).file(MultipartFileFixture.get())
                            .file(MultipartFileFixture.get()).file(MultipartFileFixture.get()).file(MultipartFileFixture.get())
                            .file(MultipartFileFixture.get()).file(MultipartFileFixture.get()).file(MultipartFileFixture.get())
                            .file(MultipartFileFixture.get()).file(MultipartFileFixture.get())
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("이미지 파일은 최소 1개, 최대 10개까지 선택 가능합니다.")));
        }

        @Test
        @DisplayName("후기가 없다면 400 상태를 반환한다.")
        void createPost_fail_EmptyContent() throws Exception {
            PostCreateRequest postCreateRequest = new PostCreateRequest(1L, List.of("카페", "한식"), null);

            MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                    objectMapper.writeValueAsBytes(postCreateRequest));

            mockMvc.perform(multipart("/api/v1/posts")
                            .file(request)
                            .file(MultipartFileFixture.get())
                            .header("Authorization", "Bearer " + accessToken)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", containsString("후기는 반드시 포함되어야 합니다.")));
        }
    }

    @Nested
    @DisplayName("프로필 게시글 목록 조회 시 ")
    class FindThumbnailsInProfile {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("유효한 요청이라면 게시글 목록을 조회한다.")
        void findThumbnailsInProfile() throws Exception {
            PageResponse<PostThumbnailResponse> response = new PageResponse<>(
                    List.of(new PostThumbnailResponse(1L, "path", LocalDateTime.now())),
                    true,
                    true,
                    false,
                    0,
                    1,
                    1,
                    1
            );
            given(postService.findThumbnailsInProfile(anyLong(), any(Pageable.class))).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/thumbnails")
                            .header("Authorization", "Bearer " + accessToken)
                            .queryParam("memberId", String.valueOf(1L))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
            PageResponse<PostThumbnailResponse> result =
                    objectMapper.readValue(jsonResponse, new TypeReference<PageResponse<PostThumbnailResponse>>() {
                    });
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @Test
        @DisplayName("멤버 ID를 파라미터로 전달하지 않으면 400 상태를 반환한다.")
        void findThumbnailsInProfileWithEmptyParam() throws Exception {
            mockMvc.perform(get("/api/v1/posts/thumbnails")
                            .header("Authorization", "Bearer " + accessToken)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("멤버 ID가 변환할 수 없는 타입이라면 400 상태를 반환한다.")
        void findThumbnailsInProfileWithInvalidType() throws Exception {
            mockMvc.perform(get("/api/v1/posts/thumbnails")
                            .header("Authorization", "Bearer " + accessToken)
                            .queryParam("memberId", "id")
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("findPostStoreMarkers 메서드는 ")
    class FindPostStoreMarkers {

        @Test
        @DisplayName("인증 정보가 존재하지 않으면 401 상태를 반환한다.")
        void notExistAuthentication() throws Exception {
            mockMvc.perform(get("/api/v1/posts/markers"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string("인증에 실패하였습니다."));
        }

        @Test
        @DisplayName("요청이 유효하다면 게시글 가게 위치 정보 목록을 응답한다.")
        void responseListOfPostStoreLocationInformation() throws Exception {
            List<PostStoreMarkerResponse> response = List.of(
                    new PostStoreMarkerResponse(
                            1L,
                            "깐부치킨",
                            "서울특별시 송파구 잠실본동 올림픽로8길 19",
                            BigDecimal.valueOf(127.65),
                            BigDecimal.valueOf(55.65)
                    ),
                    new PostStoreMarkerResponse(
                            2L,
                            "서오릉피자",
                            "서울특별시 송파구 가락로 71 빌라드그리움 1층 101,102호",
                            BigDecimal.valueOf(58.77),
                            BigDecimal.valueOf(33.77)
                    )
            );
            given(postService.findPostStoreMarkers(anyLong())).willReturn(response);

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/posts/markers")
                            .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString();
            List<PostStoreMarkerResponse> result = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<List<PostStoreMarkerResponse>>() {
                    }
            );
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }
    }
}
