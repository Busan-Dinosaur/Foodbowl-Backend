package org.dinosaur.foodbowl.domain.comment.api;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.comment.application.CommentService;
import org.dinosaur.foodbowl.domain.comment.dto.CommentCreateRequest;
import org.dinosaur.foodbowl.domain.comment.dto.CommentResponse;
import org.dinosaur.foodbowl.domain.comment.dto.CommentUpdateRequest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest extends MockApiTest {

    @MockBean
    CommentService commentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("댓글 추가는 ")
    class CreateComment {

        private final String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("정상적으로 생성되면 CREATED를 반환한다.")
        void createComment() throws Exception {
            given(commentService.save(anyLong(), any()))
                    .willReturn(1L);

            mockMvc.perform(post("/api/v1/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentCreateRequest(1L, "호이 너무 멋져")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/api/v1/posts/1"))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시글 ID 파라미터가 없으면 BAD REQUEST를 반환한다.")
        void createCommentWithNoPostId() throws Exception {
            mockMvc.perform(post("/api/v1/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentCreateRequest(null, "댓글 등록합니다.")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("게시글 ID는 반드시 포함되어야 합니다.")))
                    .andDo(print());
        }

        @Test
        @DisplayName("게시글 ID 파라미터가 음수이면 BAD REQUEST를 반환한다.")
        void createCommentWithNegativePostId() throws Exception {
            mockMvc.perform(post("/api/v1/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentCreateRequest(-1L, "댓글 등록합니다.")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("게시글 ID는 음수가 될 수 없습니다.")))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 내용이 없으면 BAD REQUEST를 반환한다.")
        void createCommentWithNoMessage() throws Exception {
            System.out.print("a".repeat(255));
            mockMvc.perform(post("/api/v1/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentCreateRequest(1L, null)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("댓글 내용은 반드시 포함되어야 합니다.")))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 길이가 255이상 이면 BAD REQUEST를 반환한다.")
        void createCommentWithWrongLengthMessage() throws Exception {
            String requestMessage = "a".repeat(256);
            mockMvc.perform(post("/api/v1/comments")
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentCreateRequest(1L, requestMessage)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("댓글은 최소 1자, 최대 255자까지 가능합니다.")))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 수정은 ")
    class UpdateComment {

        private final String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("정상적으로 수정되면 NO_CONTENT를 반환한다.")
        void updateComment() throws Exception {
            willDoNothing().given(commentService).updateComment(anyLong(), anyLong(), any(CommentUpdateRequest.class));

            mockMvc.perform(put("/api/v1/comments/{commentId}", 1L)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentUpdateRequest("안녕하세요")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("댓글 내용이 없으면 BAD REQUEST를 반환한다.")
        void updateCommentFailWithNoMessage(String message) throws Exception {
            mockMvc.perform(put("/api/v1/comments/{commentId}", 1L)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentUpdateRequest(message)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("댓글 내용은 반드시 포함되어야 합니다.")))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 ID가 양수가 아니면 BAD REQUEST를 반환한다.")
        void updateCommentFailWithInvalidId() throws Exception {
            mockMvc.perform(put("/api/v1/comments/{commentId}", -1L)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentUpdateRequest("안녕하세요")))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("댓글 ID는 양수만 가능합니다.")))
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 길이가 255이상 이면 BAD REQUEST를 반환한다.")
        void updateCommentWithWrongLengthMessage() throws Exception {
            String updateMessage = "a".repeat(256);
            mockMvc.perform(put("/api/v1/comments/{commentId}", 1L)
                            .header("Authorization", "Bearer " + token)
                            .content(objectMapper.writeValueAsString(new CommentUpdateRequest(updateMessage)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("댓글은 최소 1자, 최대 255자까지 가능합니다.")))
                    .andDo(print());
        }

        @ParameterizedTest
        @ValueSource(strings = {"hi", "1.5", "@!#"})
        @DisplayName("댓글 ID가 Long 타입이 아니면 BAD REQUEST를 반환한다.")
        void updateCommentFailWithNoId(String commentId) throws Exception {
            mockMvc.perform(put("/api/v1/comments/{commentId}", commentId)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("commentId의 타입이 잘못되었습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 삭제는 ")
    class DeleteComment {

        private final String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("정상적으로 삭제되면 NO CONTENT를 반환한다.")
        void deleteSuccess() throws Exception {
            willDoNothing().given(commentService).deleteComment(anyLong(), anyLong());

            mockMvc.perform(delete("/api/v1/comments/{commentId}", 1L)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        @DisplayName("댓글 ID가 양수가 아니면 BAD REQUEST를 반환한다.")
        void deleteCommentFailWithInvalidId() throws Exception {
            mockMvc.perform(delete("/api/v1/comments/{commentId}", -1L)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(containsString("댓글 ID는 양수만 가능합니다.")))
                    .andDo(print());
        }

        @ParameterizedTest
        @ValueSource(strings = {"hi", "1.5", "@!#"})
        @DisplayName("댓글 ID가 Long 타입이 아니면 BAD REQUEST를 반환한다.")
        void deleteCommentFailWithInvalidTypeId(String commentId) throws Exception {
            mockMvc.perform(delete("/api/v1/comments/{commentId}", commentId)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("commentId의 타입이 잘못되었습니다."))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("게시글에 존재하는 모든 댓글 조회는 ")
    class FindAllComments {

        private final String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("정상적으로 조회되면 OK를 반환한다.")
        void findAllComments() throws Exception {
            PageResponse<CommentResponse> commentResponses = new PageResponse<>(
                    List.of(new CommentResponse(1L, 1L, 1L, "gray", "path", "그레이 댓글입니다.", LocalDateTime.now(),
                            LocalDateTime.now())),
                    true,
                    true,
                    false,
                    1,
                    1,
                    1,
                    1
            );
            given(commentService.findAllCommentsInPost(anyLong(), any(Pageable.class))).willReturn(commentResponses);

            mockMvc.perform(get("/api/v1/comments")
                            .queryParam("postId", "1")
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("게시글 ID가 양수가 아니면 BAD REQUEST를 반환한다.")
        void findAllCommentsFailWithNegativeId() throws Exception {
            mockMvc.perform(get("/api/v1/comments")
                            .queryParam("postId", "-1")
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("게시글 ID는 양수만 가능합니다."));
        }

        @ParameterizedTest
        @ValueSource(strings = {"hi", "1.5", "@!#"})
        @DisplayName("게시글 ID가 Long 타입이 아니면 BAD REQUEST를 반환한다.")
        void findAllCommentsFailWithInvalidTypeId(String postId) throws Exception {
            mockMvc.perform(get("/api/v1/comments")
                            .queryParam("postId", postId)
                            .header("Authorization", "Bearer " + token)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("postId의 타입이 잘못되었습니다."));
        }
    }
}
