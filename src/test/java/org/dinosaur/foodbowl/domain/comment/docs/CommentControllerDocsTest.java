package org.dinosaur.foodbowl.domain.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.comment.api.CommentController;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    @DisplayName("등록을 문서화한다.")
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
}
