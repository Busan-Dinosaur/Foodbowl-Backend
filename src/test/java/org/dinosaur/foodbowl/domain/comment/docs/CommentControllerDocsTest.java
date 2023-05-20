package org.dinosaur.foodbowl.domain.comment.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import java.nio.charset.StandardCharsets;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.comment.api.CommentController;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = CommentController.class)
public class CommentControllerDocsTest extends MockApiTest {

    @MockBean
    CommentService commentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("댓글 등록을 문서화한다.")
    void createComment() throws Exception {
        String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
        given(commentService.save(anyLong(), any()))
                .willReturn(1L);

        mockMvc.perform(post("/comments")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(new CommentCreateRequest(1L, "다즐 너무 멋져")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(header().string("Location", "/comments/1"))
                .andDo(document("api-v1-comments",
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
}
