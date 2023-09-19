package org.dinosaur.foodbowl.domain.blame.presentation;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dinosaur.foodbowl.domain.auth.application.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.domain.blame.application.BlameService;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.dto.request.BlameRequest;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.domain.vo.RoleType;
import org.dinosaur.foodbowl.test.PresentationTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("NonAsciiCharacters")
@WebMvcTest(BlameController.class)
class BlameControllerTest extends PresentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BlameService blameService;

    @Nested
    class 신고_시 {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        void 정상적인_요청이라면_200_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            BlameRequest request = new BlameRequest(1L, BlameTarget.MEMBER.name(), "부적절한 닉네임");
            willDoNothing().given(blameService).blame(any(BlameRequest.class), any(Member.class));

            mockMvc.perform(post("/v1/blames")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        void 신고_대상_ID가_양의_정수가_아니라면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            BlameRequest request = new BlameRequest(-1L, BlameTarget.MEMBER.name(), "부적절한 닉네임");

            mockMvc.perform(post("/v1/blames")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("신고 대상 ID는 양의 정수만 가능합니다.")));
            ;
        }

        @Test
        void 신고_대상_타입이_올바르지_않으면_400_응답을_반환한다() throws Exception {
            mockingAuthMemberInResolver();
            String content = """
                    "targetId":-1,"blameTarget":"HELLO","description":"부적절한 닉네임"
                    """;

            mockMvc.perform(post("/v1/blames")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void 신고_사유가_공백이거나_존재하지_않으면_400_응답을_반환한다(String description) throws Exception {
            mockingAuthMemberInResolver();
            BlameRequest request = new BlameRequest(1L, BlameTarget.MEMBER.name(), description);

            mockMvc.perform(post("/v1/blames")
                            .header(AUTHORIZATION, BEARER + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errorCode").value("CLIENT-100"))
                    .andExpect(jsonPath("$.message", containsString("신고 사유가 공백이거나 존재하지 않습니다.")));
        }
    }
}
