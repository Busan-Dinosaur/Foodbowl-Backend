package org.dinosaur.foodbowl.domain.bookmark.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.bookmark.application.BookmarkService;
import org.dinosaur.foodbowl.domain.bookmark.dto.response.BookmarkThumbnailResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(BookmarkController.class)
class BookmarkControllerTest extends MockApiTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookmarkService bookmarkService;

    @Nested
    @DisplayName("프로필 북마크 썸네일 목록 조회 시 ")
    class FindThumbnailsInProfile {

        private String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("유효한 요청이라면 게시글 목록을 조회한다.")
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

            MvcResult mvcResult = mockMvc.perform(get("/api/v1/bookmarks/thumbnails")
                            .header("Authorization", "Bearer " + accessToken)
                            .queryParam("memberId", String.valueOf(1L)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            String jsonResponse = mvcResult.getResponse().getContentAsString(Charset.forName("UTF-8"));
            PageResponse<BookmarkThumbnailResponse> result = objectMapper.readValue(
                    jsonResponse,
                    new TypeReference<PageResponse<BookmarkThumbnailResponse>>() {
                    }
            );
            assertThat(result).usingRecursiveComparison().isEqualTo(response);
        }

        @Test
        @DisplayName("멤버 ID를 파라미터로 전달하지 않으면 400 상태를 반환한다.")
        void findThumbnailsInProfileWithEmptyParam() throws Exception {
            mockMvc.perform(get("/api/v1/bookmarks/thumbnails")
                            .header("Authorization", "Bearer " + accessToken))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("멤버 ID가 변환할 수 없는 타입이라면 400 상태를 반환한다.")
        void findThumbnailsInProfileWithInvalidType() throws Exception {
            mockMvc.perform(get("/api/v1/bookmarks/thumbnails")
                            .header("Authorization", "Bearer " + accessToken)
                            .queryParam("memberId", "id"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
