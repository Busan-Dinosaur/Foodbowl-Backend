package org.dinosaur.foodbowl.domain.post.docs;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.domain.post.api.PostController;
import org.dinosaur.foodbowl.domain.post.application.PostService;
import org.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequest;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.testsupport.fixture.MultipartFileFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

@WebMvcTest(PostController.class)
public class PostControllerDocsTest extends MockApiTest {

    @MockBean
    private PostService postService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("게시글 생성을 문서화한다.")
    void createPost() throws Exception {
        PostCreateRequest postCreateRequest = new PostCreateRequest(1L, List.of("카페", "한식"), "맛있다");

        given(postService.save(anyLong(), any(PostCreateRequest.class), anyList())).willReturn(1L);

        MockMultipartFile request = new MockMultipartFile("request", "", "application/json",
                objectMapper.writeValueAsString(postCreateRequest).getBytes(StandardCharsets.UTF_8));

        var headerDescriptors = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 액세스 토큰")
        };

        mockMvc.perform(multipart("/api/v1/posts")
                        .file(request)
                        .file(MultipartFileFixture.get())
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("/api/v1/posts/1"))
                .andDo(document("api-v1-posts",
                        requestHeaders(
                                headerDescriptors
                        ),
                        requestParts(
                                partWithName("request").description("게시글 정보"),
                                partWithName("imageFiles").description("게시글 사진들")
                        ),
                        requestPartFields("request",
                                fieldWithPath("storeId").description("가게 ID"),
                                fieldWithPath("categoryNames").description("카테고리"),
                                fieldWithPath("content").description("후기")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("생성된 게시글의 URI")
                        )
                ));
    }

    @Test
    @DisplayName("프로필 게시글 목록 조회를 문서화한다.")
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

        var headerDescriptors = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
        };

        var parameterDescriptors = new ParameterDescriptor[]{
                parameterWithName("memberId").description("프로필 멤버 ID"),
                parameterWithName("page").optional().description("프로필 게시글 썸네일 목록 페이지 숫자 (입력하지 않으면, default = 0)"),
                parameterWithName("size").optional().description("프로필 게시글 썸네일 목록 데이터 개수 (입력하지 않으면, default = 18)")
        };

        var responseFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("content").description("게시글 썸네일 정보 목록"),
                fieldWithPath("content[].postId").description("게시글 ID"),
                fieldWithPath("content[].thumbnailPath").description("썸네일 경로"),
                fieldWithPath("content[].createdAt").description("게시글 작성 시간"),
                fieldWithPath("first").description("첫 페이지 여부"),
                fieldWithPath("last").description("마지막 페이지 여부"),
                fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                fieldWithPath("currentPage").description("현재 페이지 인덱스"),
                fieldWithPath("currentElementSize").description("현재 데이터 개수"),
                fieldWithPath("totalPage").description("전체 페이지 숫자"),
                fieldWithPath("totalElementSize").description("전체 데이터 개수"),
        };

        mockMvc.perform(get("/api/v1/posts/thumbnails")
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .queryParam("memberId", String.valueOf(1L))
                        .queryParam("page", String.valueOf(0))
                        .queryParam("size", String.valueOf(18)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].postId").value(1L))
                .andExpect(jsonPath("$.content[0].thumbnailPath").value("path"))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andExpect(jsonPath("$.currentPage").value(0))
                .andExpect(jsonPath("$.currentElementSize").value(1))
                .andExpect(jsonPath("$.totalPage").value(1))
                .andExpect(jsonPath("$.totalElementSize").value(1))
                .andDo(document("api-v1-posts-thumbnails",
                        requestHeaders(
                                headerDescriptors
                        ),
                        queryParameters(
                                parameterDescriptors
                        ),
                        responseFields(
                                responseFieldDescriptors
                        )
                ));
    }
}
