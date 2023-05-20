package org.dinosaur.foodbowl.domain.member.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.api.MemberController;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.request.ProfileUpdateRequest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;

@WebMvcTest(controllers = MemberController.class)
public class MemberControllerDocsTest extends MockApiTest {

    @MockBean
    private MemberService memberService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("회원 탈퇴를 문서화한다.")
    void withDraw() throws Exception {
        willDoNothing().given(memberService).withDraw(anyLong());

        var headerDescriptors = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
        };

        mockMvc.perform(delete("/api/v1/members")
                        .header(HttpHeaders.AUTHORIZATION,
                                "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                )
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("api-v1-members-delete",
                        requestHeaders(
                                headerDescriptors
                        )
                ));
    }

    @Test
    @DisplayName("닉네임, 소개 수정을 문서화한다.")
    void updateProfile() throws Exception {
        String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);
        ProfileUpdateRequest request = new ProfileUpdateRequest("foodbowl", "Foodbowl is Good");

        willDoNothing().given(memberService).updateProfile(anyLong(), any(ProfileUpdateRequest.class));

        var headerDescriptors = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
        };

        var requestFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("nickname").description("수정할 닉네임(수정하지 않을 시 기존 닉네임)"),
                fieldWithPath("introduction").description("수정할 소개(수정하지 않을 시 기존 소개)").optional(),
        };

        mockMvc.perform(put("/api/v1/members")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent())
                .andDo(document("api-v1-members-update-profile",
                        requestHeaders(
                                headerDescriptors
                        ),
                        requestFields(
                                requestFieldDescriptors
                        )
                ));
    }
}
