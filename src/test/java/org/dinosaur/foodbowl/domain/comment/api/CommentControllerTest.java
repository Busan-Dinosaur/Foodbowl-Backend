package org.dinosaur.foodbowl.domain.comment.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends MockApiTest {

    @MockBean
    CommentService commentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("댓글 추가는")
    class CreateComment {

        private final String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("정상적으로 생성되면 CREATED를 반환한다.")
        void createComment() throws Exception {
            given(commentService.save(any(), any()))
                    .willReturn(new CommentResponse(1L, 1L, "호이 너무 멋져", LocalDateTime.now(), LocalDateTime.now()));

            mockMvc.perform(post("/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentRequest(1L, "호이 너무 멋져")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/comments/1"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시글 ID 파라미터가 없으면 BAD REQUEST를 반환한다.")
        void createCommentWithNoPostId() throws Exception {
            mockMvc.perform(post("/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentRequest(null, "댓글 등록합니다.")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("게시글 ID 파라미터가 음수이면 BAD REQUEST를 반환한다.")
        void createCommentWithNegativePostId() throws Exception {
            mockMvc.perform(post("/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentRequest(-1L, "댓글 등록합니다.")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 내용이 없으면 BAD REQUEST를 반환한다.")
        void createCommentWithNoMessage() throws Exception {
            mockMvc.perform(post("/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentRequest(1L, null)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

}
