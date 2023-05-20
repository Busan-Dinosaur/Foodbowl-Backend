package org.dinosaur.foodbowl.domain.bookmark.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.bookmark.api.BookmarkController;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkService;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;

@WebMvcTest(BookmarkController.class)
class BookmarkControllerDocsTest extends MockApiTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private BookmarkService bookmarkService;

    @Test
    @DisplayName("프로필 북마크 게시글 썸네일 목록 조회를 문서화한다.")
    void findThumbnailsInProfile() throws Exception {
        PageResponse<BookmarkThumbnailResponse> response = new PageResponse<>(
                List.of(new BookmarkThumbnailResponse(1L, 1L, "path", LocalDateTime.now())),
                true,
                true,
                false,
                0,
                1,
                1,
                1
        );
        given(bookmarkService.findThumbnailsInProfile(anyLong(), any(Pageable.class))).willReturn(response);

        var headerDescriptors = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
        };

        var parameterDescriptors = new ParameterDescriptor[]{
                parameterWithName("memberId").description("프로필 멤버 ID"),
                parameterWithName("page").optional().description("프로필 북마크 게시글 썸네일 목록 페이지 숫자 (입력하지 않으면, default = 0)"),
                parameterWithName("size").optional().description("프로필 북마크 게시글 썸네일 목록 데이터 개수 (입력하지 않으면, default = 18)")
        };

        var responseFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("content").description("북마크 게시글 썸네일 정보 목록"),
                fieldWithPath("content[].bookmarkId").description("북마크 ID"),
                fieldWithPath("content[].postId").description("북마크 게시글 ID"),
                fieldWithPath("content[].thumbnailPath").description("북마크 게시글 썸네일 경로"),
                fieldWithPath("content[].createdAt").description("북마크 게시글 작성 시간"),
                fieldWithPath("first").description("첫 페이지 여부"),
                fieldWithPath("last").description("마지막 페이지 여부"),
                fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                fieldWithPath("currentPage").description("현재 페이지 인덱스"),
                fieldWithPath("currentElementSize").description("현재 데이터 개수"),
                fieldWithPath("totalPage").description("전체 페이지 숫자"),
                fieldWithPath("totalElementSize").description("전체 데이터 개수"),
        };

        mockMvc.perform(get("/api/v1/bookmarks/thumbnails")
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .queryParam("memberId", String.valueOf(1L)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("api-v1-bookmarks-thumbnails",
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
