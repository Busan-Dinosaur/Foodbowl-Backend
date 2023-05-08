package org.dinosaur.foodbowl.domain.member.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.api.MemberController;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.request.DuplicationCheckRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.DuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

@WebMvcTest(controllers = MemberController.class)
public class MemberControllerDocsTest extends MockApiTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final DuplicationCheckRequest request = new DuplicationCheckRequest("gray");

    @Test
    @DisplayName("닉네임 중복 검증을 문서화한다.")
    void appleLogin() throws Exception {
        String token = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
        given(memberService.checkDuplicate(any())).willReturn(new DuplicateCheckResponse(false));

        mockMvc.perform(post("/api/v1/members/check-nicknames")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("api-v1-members-check-nicknames",
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임")
                        ),
                        responseFields(
                                fieldWithPath("hasDuplicate").type(JsonFieldType.BOOLEAN).description("닉네임 중복 여부")
                        )));
    }
}
