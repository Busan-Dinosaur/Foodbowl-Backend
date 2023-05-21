package org.dinosaur.foodbowl.domain.store.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.domain.store.api.StoreController;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
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
import org.springframework.restdocs.payload.JsonFieldType;

@WebMvcTest(StoreController.class)
public class StoreControllerDocsTest extends MockApiTest {

    @MockBean
    private StoreService storeService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("가게 조회 응답을 반환한다.")
    void findSuccess() throws Exception {
        String address = "서울시+송파구+올림픽로+473";
        StoreResponse storeResponse = createResponse();
        given(storeService.findByAddress(any(String.class))).willReturn(storeResponse);

        mockMvc.perform(get("/api/v1/stores")
                        .queryParam("address", address)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("api-v1-stores-read",
                        requestHeaders(
                                headerWithName("Authorization").description("Foodbowl 인증 토큰")
                        ),
                        queryParameters(
                                parameterWithName("address").description("주소(공백은 반드시 '+'로 치환해야 합니다.")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("가게 ID"),
                                fieldWithPath("storeName").type(JsonFieldType.STRING).description("가게 이름"),
                                fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소"),
                                fieldWithPath("region1depthName").type(JsonFieldType.STRING).description("광역시 또는 도"),
                                fieldWithPath("region2depthName").type(JsonFieldType.STRING).description("시/군/구"),
                                fieldWithPath("region3depthName").type(JsonFieldType.STRING).description("읍/면/동"),
                                fieldWithPath("roadName").type(JsonFieldType.STRING).description("도로명 주소"),
                                fieldWithPath("undergroundYN").type(JsonFieldType.STRING).description("지하 여부"),
                                fieldWithPath("mainBuildingNo").type(JsonFieldType.STRING).description("건물 번호"),
                                fieldWithPath("subBuildingNo").type(JsonFieldType.STRING).description("동/호수"),
                                fieldWithPath("buildingName").type(JsonFieldType.STRING).description("건물 이름"),
                                fieldWithPath("zoneNo").type(JsonFieldType.STRING).description("우편 번호"),
                                fieldWithPath("x").type(JsonFieldType.NUMBER).description("경도"),
                                fieldWithPath("y").type(JsonFieldType.NUMBER).description("위도")
                        )));
    }

    @Test
    @DisplayName("가게를 생성한다.")
    void createSuccess() throws Exception {
        StoreRequest storeRequest = new StoreRequest(
                "신천직화집",
                "서울시 송파구 올림픽로 473",
                "서울시",
                "송파구",
                "신천동",
                "올림픽로",
                "N",
                "473",
                "14층 1400호",
                "루터회관",
                "12345",
                BigDecimal.valueOf(127.3435356),
                BigDecimal.valueOf(37.12314545)
        );
        StoreResponse storeResponse = createResponse();
        given(storeService.save(any(StoreRequest.class))).willReturn(storeResponse);

        var headerDescriptors = new HeaderDescriptor[]{
                headerWithName(HttpHeaders.AUTHORIZATION).description("서버에서 발급한 엑세스 토큰")
        };

        var requestFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("storeName").type(JsonFieldType.STRING).description("가게 이름"),
                fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소"),
                fieldWithPath("region1depthName").type(JsonFieldType.STRING).description("광역시 또는 도"),
                fieldWithPath("region2depthName").type(JsonFieldType.STRING).description("시/군/구"),
                fieldWithPath("region3depthName").type(JsonFieldType.STRING).description("읍/면/동"),
                fieldWithPath("roadName").type(JsonFieldType.STRING).description("도로명 주소").optional(),
                fieldWithPath("undergroundYN").type(JsonFieldType.STRING).description("지하 여부").optional(),
                fieldWithPath("mainBuildingNo").type(JsonFieldType.STRING).description("건물 번호").optional(),
                fieldWithPath("subBuildingNo").type(JsonFieldType.STRING).description("동/호수").optional(),
                fieldWithPath("buildingName").type(JsonFieldType.STRING).description("건물 이름").optional(),
                fieldWithPath("zoneNo").type(JsonFieldType.STRING).description("우편 번호").optional(),
                fieldWithPath("x").type(JsonFieldType.NUMBER).description("경도"),
                fieldWithPath("y").type(JsonFieldType.NUMBER).description("위도")
        };

        var responseFieldDescriptors = new FieldDescriptor[]{
                fieldWithPath("id").type(JsonFieldType.NUMBER).description("가게 ID"),
                fieldWithPath("storeName").type(JsonFieldType.STRING).description("가게 이름"),
                fieldWithPath("addressName").type(JsonFieldType.STRING).description("주소"),
                fieldWithPath("region1depthName").type(JsonFieldType.STRING).description("광역시 또는 도"),
                fieldWithPath("region2depthName").type(JsonFieldType.STRING).description("시/군/구"),
                fieldWithPath("region3depthName").type(JsonFieldType.STRING).description("읍/면/동"),
                fieldWithPath("roadName").type(JsonFieldType.STRING).description("도로명 주소"),
                fieldWithPath("undergroundYN").type(JsonFieldType.STRING).description("지하 여부"),
                fieldWithPath("mainBuildingNo").type(JsonFieldType.STRING).description("건물 번호"),
                fieldWithPath("subBuildingNo").type(JsonFieldType.STRING).description("동/호수"),
                fieldWithPath("buildingName").type(JsonFieldType.STRING).description("건물 이름"),
                fieldWithPath("zoneNo").type(JsonFieldType.STRING).description("우편 번호"),
                fieldWithPath("x").type(JsonFieldType.NUMBER).description("경도"),
                fieldWithPath("y").type(JsonFieldType.NUMBER).description("위도")
        };

        mockMvc.perform(post("/api/v1/stores")
                .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                .content(objectMapper.writeValueAsString(storeRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("api-v1-stores-create",
                        requestHeaders(
                                headerDescriptors
                        ),
                        requestFields(
                                requestFieldDescriptors
                        ),
                        responseFields(
                                responseFieldDescriptors
                        )));
    }

    private StoreResponse createResponse() {
        return new StoreResponse(
                1L,
                "신천직화집",
                "서울시 송파구 올림픽로 473",
                "서울시",
                "송파구",
                "신천동",
                "올림픽로",
                "N",
                "473",
                "14층 1400호",
                "루터회관",
                "12345",
                BigDecimal.valueOf(127.3435356),
                BigDecimal.valueOf(37.12314545)
        );
    }
}
