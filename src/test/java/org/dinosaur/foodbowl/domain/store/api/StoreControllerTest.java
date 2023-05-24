package org.dinosaur.foodbowl.domain.store.api;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.STORE_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.member.entity.Role.RoleType;
import org.dinosaur.foodbowl.domain.store.application.StoreService;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.global.config.security.jwt.JwtTokenProvider;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest extends MockApiTest {

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

        ResultActions resultActions = mockMvc.perform(post("/api/v1/stores")
                .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                .content(objectMapper.writeValueAsString(storeRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(storeResponse.getId()))
                .andExpect(jsonPath("$.storeName").value(storeResponse.getStoreName()))
                .andExpect(jsonPath("$.addressName").value(storeResponse.getAddressName()))
                .andExpect(jsonPath("$.region1depthName").value(storeResponse.getRegion1depthName()))
                .andExpect(jsonPath("$.region2depthName").value(storeResponse.getRegion2depthName()))
                .andExpect(jsonPath("$.region3depthName").value(storeResponse.getRegion3depthName()))
                .andExpect(jsonPath("$.roadName").value(storeResponse.getRoadName()))
                .andExpect(jsonPath("$.undergroundYN").value(storeResponse.getUndergroundYN()))
                .andExpect(jsonPath("$.mainBuildingNo").value(storeResponse.getMainBuildingNo()))
                .andExpect(jsonPath("$.subBuildingNo").value(storeResponse.getSubBuildingNo()))
                .andExpect(jsonPath("$.buildingName").value(storeResponse.getBuildingName()))
                .andExpect(jsonPath("$.zoneNo").value(storeResponse.getZoneNo()))
                .andExpect(jsonPath("$.x").value(storeResponse.getX()))
                .andExpect(jsonPath("$.y").value(storeResponse.getY()));
    }

    private void execute(StoreRequest storeRequest, String token) throws Exception {
        mockMvc.perform(post("/api/v1/stores")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(storeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
                BigDecimal.valueOf(37.12314545),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Nested
    @DisplayName("가게를 조회할 때 ")
    class FindStore {

        @Test
        @DisplayName("응답을 반환한다.")
        void findSuccess() throws Exception {
            String address = "서울시+송파구+올림픽로+473";
            StoreResponse storeResponse = createResponse();
            given(storeService.findByAddress(any(String.class))).willReturn(storeResponse);

            mockMvc.perform(get("/api/v1/stores")
                            .queryParam("address", address)
                            .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("요청에 주소가 없으면 BAD REQUEST를 반환한다.")
        void findFailWithEmptyAddress(String address) throws Exception {
            mockMvc.perform(get("/api/v1/stores")
                            .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                            .queryParam("address", address)
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @Test
        @DisplayName("주소에 해당하는 가게가 없으면 NOT FOUND를 반환한다.")
        void findFailWithNoResult() throws Exception {
            String address = "부산시+송파구+올림픽로+473";
            given(storeService.findByAddress(any(String.class))).willThrow(new FoodbowlException(STORE_NOT_FOUND));

            mockMvc.perform(get("/api/v1/stores")
                            .queryParam("address", address)
                            .header("Authorization", "Bearer " + jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원))
                            .characterEncoding(StandardCharsets.UTF_8))
                    .andExpect(status().isNotFound())
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("가게 생성 요청에")
    class StoreWithRequest {

        private final String accessToken = jwtTokenProvider.createAccessToken(1L, RoleType.ROLE_회원);

        @Test
        @DisplayName("가게 이름이 없는 경우 BAD REQUEST가 반환된다.")
        void createByEmptyStoreName() throws Exception {
            StoreRequest storeRequest = new StoreRequest(
                    null,
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

            execute(storeRequest, accessToken);
        }

        @Test
        @DisplayName("주소가 없는 경우 BAD REQUEST가 반환된다.")
        void createByWrongAddress() throws Exception {
            StoreRequest storeRequest = new StoreRequest(
                    "신천직화집",
                    null,
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

            execute(storeRequest, accessToken);
        }

        @Test
        @DisplayName("광역시 또는 도가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongRegion1depthName() throws Exception {
            StoreRequest storeRequest = new StoreRequest(
                    "신천직화집",
                    "서울시 송파구 올림픽로 473",
                    null,
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

            execute(storeRequest, accessToken);
        }

        @Test
        @DisplayName("시/군/구가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongRegion2depthName() throws Exception {
            StoreRequest storeRequest = new StoreRequest(
                    "신천직화집",
                    "서울시 송파구 올림픽로 473",
                    "서울시",
                    null,
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

            execute(storeRequest, accessToken);
        }

        @Test
        @DisplayName("읍/면/동이 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongRegion3depthName() throws Exception {
            StoreRequest storeRequest = new StoreRequest(
                    "신천직화집",
                    "서울시 송파구 올림픽로 473",
                    "서울시",
                    "송파구",
                    null,
                    "올림픽로",
                    "N",
                    "473",
                    "14층 1400호",
                    "루터회관",
                    "12345",
                    BigDecimal.valueOf(127.3435356),
                    BigDecimal.valueOf(37.12314545)
            );

            execute(storeRequest, accessToken);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "s", "@", "y", "n", "1"})
        @DisplayName("지하 여부가 Y or N이 아닌 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongUnderground(String underground) throws Exception {
            StoreRequest storeRequest = new StoreRequest(
                    "신천직화집",
                    "서울시 송파구 올림픽로 473",
                    "서울시",
                    "송파구",
                    "신천동",
                    "올림픽로",
                    underground,
                    "473",
                    "14층 1400호",
                    "루터회관",
                    "12345",
                    BigDecimal.valueOf(127.3435356),
                    BigDecimal.valueOf(37.12314545)
            );
            execute(storeRequest, accessToken);
        }

        @Test
        @DisplayName("경도가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByEmptyX() throws Exception {
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
                    null,
                    BigDecimal.valueOf(37.12314545)
            );
            execute(storeRequest, accessToken);
        }

        @Test
        @DisplayName("위도가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByEmptyY() throws Exception {
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
                    null
            );
            execute(storeRequest, accessToken);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-181", "181"})
        @DisplayName("경도가 범위를 벗어나는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongX(String x) throws Exception {
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
                    new BigDecimal(x),
                    BigDecimal.valueOf(37.12314545)
            );
            execute(storeRequest, accessToken);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-91", "91"})
        @DisplayName("위도가 범위를 벗어나는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongY(String y) throws Exception {
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
                    new BigDecimal(y)
            );

            execute(storeRequest, accessToken);
        }
    }
}
