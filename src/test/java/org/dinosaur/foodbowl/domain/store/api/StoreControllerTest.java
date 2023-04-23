package org.dinosaur.foodbowl.domain.store.api;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.dinosaur.foodbowl.MockApiTest;
import org.dinosaur.foodbowl.domain.store.dto.StoreRequest;
import org.dinosaur.foodbowl.domain.store.dto.StoreResponse;
import org.dinosaur.foodbowl.domain.store.service.StoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = StoreController.class)
class StoreControllerTest extends MockApiTest {

    @MockBean
    StoreService storeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("가게를 조회한다.")
    void findSuccess() throws Exception {
        Long id = 1L;
        StoreResponse storeResponse = createResponse();
        when(storeService.findOne(id)).thenReturn(storeResponse);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/stores/{id}", id)
                .with(csrf())
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

    @Test
    @DisplayName("모든 가게를 조회한다.")
    void findAllSuccess() throws Exception {
        StoreResponse storeResponse1 = createResponse();
        StoreResponse storeResponse2 = createResponse();
        List<StoreResponse> storeResponses = List.of(storeResponse1, storeResponse2);
        when(storeService.findAll()).thenReturn(storeResponses);

        ResultActions resultActions = mockMvc.perform(get("/api/v1/stores")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(storeResponses.size()))
                .andDo(print());
    }

    @Test
    @DisplayName("가게를 생성한다.")
    void createSuccess() throws Exception {
        StoreRequest storeRequest = createRequest();
        StoreResponse storeResponse = createResponse();
        when(storeService.save(any())).thenReturn(storeResponse);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/stores")
                .with(csrf())
                .content(objectMapper.writeValueAsString(storeRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8));

        resultActions.andExpect(status().isCreated())
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

    @Nested
    @DisplayName("가게 생성 요청에")
    class StoreWithRequest {
        @Test
        @DisplayName("가게 이름이 없는 경우 BAD REQUEST가 반환된다.")
        void createByEmptyStoreName() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setStoreName(null);

            execute(storeRequest);
        }

        @Test
        @DisplayName("주소가 없는 경우 BAD REQUEST가 반환된다.")
        void createByWrongAddress() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setAddressName(null);

            execute(storeRequest);
        }

        @Test
        @DisplayName("광역시 또는 도가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongRegion1depthName() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setRegion1depthName(null);

            execute(storeRequest);
        }

        @Test
        @DisplayName("시/군/구가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongRegion2depthName() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setRegion2depthName(null);

            execute(storeRequest);
        }

        @Test
        @DisplayName("읍/면/동이 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongRegion3depthName() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setRegion3depthName(null);

            execute(storeRequest);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "s", "@", "y", "n", "1"})
        @DisplayName("지하 여부가 Y or N이 아닌 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongUnderground(String underground) throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setUndergroundYN(underground);

            execute(storeRequest);
        }

        @Test
        @DisplayName("경도가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByEmptyX() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setX(null);

            execute(storeRequest);
        }

        @Test
        @DisplayName("위도가 없는 경우 BAD REQUEST가 반환된다.")
        void createStoreByEmptyY() throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setY(null);

            execute(storeRequest);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-181", "181"})
        @DisplayName("경도가 범위를 벗어나는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongX(String x) throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setX(new BigDecimal(x));

            execute(storeRequest);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-91", "91"})
        @DisplayName("위도가 범위를 벗어나는 경우 BAD REQUEST가 반환된다.")
        void createStoreByWrongY(String y) throws Exception {
            StoreRequest storeRequest = createRequest();
            storeRequest.setY(new BigDecimal(y));

            execute(storeRequest);
        }
    }

    private StoreRequest createRequest() {
        return new StoreRequest(
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

    private void execute(StoreRequest storeRequest) throws Exception {
        mockMvc.perform(post("/api/v1/stores")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(storeRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
