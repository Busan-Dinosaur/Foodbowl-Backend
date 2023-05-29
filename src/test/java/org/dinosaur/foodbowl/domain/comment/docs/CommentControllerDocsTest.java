package org.dinosaur.foodbowl.domain.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.comment.api.CommentController;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentResponse;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
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
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

@WebMvcTest(controllers = CommentController.class)
public class CommentControllerDocsTest extends MockApiTest {

    @MockBean
    CommentService commentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("댓글 등록을 문서화한다.")
    void createComment() throws Exception {
        given(commentService.save(anyLong(), any(CommentCreateRequest.class))).willReturn(1L);

        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest(1L, "다즐 너무 멋져")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/posts/1"))
                .andDo(document("api-v1-comments-create",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                                ),
                                requestFields(
                                        fieldWithPath("postId").type(JsonFieldType.NUMBER).description("게시글 ID"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("댓글 내용")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 수정을 문서화한다.")
    void updateComment() throws Exception {
        willDoNothing().given(commentService).updateComment(anyLong(), anyLong(), any(CommentUpdateRequest.class));

        mockMvc.perform(put("/api/v1/comments/{commentId}", 1L)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .content(objectMapper.writeValueAsString(new CommentUpdateRequest("안녕하세요")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNoContent())
                .andDo(document("api-v1-comments-update",
                                requestHeaders(
                                        headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                                ),
                                pathParameters(
                                        parameterWithName("commentId").description("수정할 댓글 ID")
                                ),
                                requestFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("수정할 댓글 내용")
                                )
                        )
                );
    }

    @Test
    @DisplayName("댓글 삭제를 문서화한다.")
    void deleteComment() throws Exception {
        willDoNothing().given(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/api/v1/comments/{commentId}", 1L)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isNoContent())
                .andDo(document("api-v1-comments-delete",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("commentId").description("삭제할 댓글 ID")
                        )
                ));
    }

    @Test
    @DisplayName("게시글에 있는 모든 댓글 조회를 문서화한다.")
    void findAllCommentsInPost() throws Exception {
        PageResponse<CommentResponse> commentResponses = new PageResponse<>(
                List.of(new CommentResponse(1L, 1L, 1L, "gray",
                        "http://image.url/gray.jpg", "그레이 댓글입니다.", LocalDateTime.now(), LocalDateTime.now())),
                true,
                true,
                false,
                1,
                1,
                1,
                1
        );
        given(commentService.findAllCommentsInPost(anyLong(), any(Pageable.class))).willReturn(commentResponses);

        var responseFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("content").description("게시글 댓글 목록"),
                fieldWithPath("content[].commentId").description("댓글 ID"),
                fieldWithPath("content[].postId").description("게시글 ID"),
                fieldWithPath("content[].memberId").description("댓글 작성 회원 ID"),
                fieldWithPath("content[].memberNickname").description("댓글 작성 회원 닉네임"),
                fieldWithPath("content[].memberThumbnailPath").description("댓글 작성 회원 프로필 썸네일 Path"),
                fieldWithPath("content[].message").description("댓글 내용"),
                fieldWithPath("content[].createdAt").description("댓글 작성 시간"),
                fieldWithPath("content[].updatedAt").description("댓글 최종 수정 시간"),
                fieldWithPath("first").description("첫 페이지 여부"),
                fieldWithPath("last").description("마지막 페이지 여부"),
                fieldWithPath("hasNext").description("다음 페이지 존재 여부"),
                fieldWithPath("currentPageIndex").description("현재 페이지 인덱스"),
                fieldWithPath("currentElementSize").description("현재 데이터 개수"),
                fieldWithPath("totalPage").description("전체 페이지 숫자"),
                fieldWithPath("totalElementCount").description("전체 데이터 개수"),
        };

        mockMvc.perform(get("/api/v1/comments")
                        .queryParam("postId", "1")
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(document("api-v1-comments-findAllInPost",
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
                        ),
                        queryParameters(
                                parameterWithName("postId").description("댓글을 조회하는 게시글 ID"),
                                parameterWithName("page").optional()
                                        .description("게시글에 존재하는 댓글 페이지 숫자 (입력하지 않으면, default = 0)"),
                                parameterWithName("size").optional()
                                        .description("게시글에 존재하는 댓글 데이터 개수 (입력하지 않으면, default = 18)")
                        ),
                        responseFields(
                                responseFieldDescriptors
                        )
                ));
    }
}
