package org.dinosaur.foodbowl.domain.store.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.SchoolsResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponses;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponses;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "가게", description = "가게 API")
public interface StoreControllerDocs {

    @Operation(
            summary = "카테고리 목록 조회",
            description = "카테고리 목록을 조회한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "카테고리 목록 조회 성공"
    )
    ResponseEntity<CategoriesResponse> getCategories();

    @Operation(
            summary = "학교 목록 조회",
            description = "DB에 존재하는 학교 목록을 조회한다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "학교 목록 조회 성공"
    )
    ResponseEntity<SchoolsResponse> getSchools();

    @Operation(
            summary = "가게 검색 결과 조회",
            description = """
                    키워드로 가게를 검색합니다.
                                        
                    필수 파라미터: name(검색 키워드), x(경도), y(위도)
                                        
                    선택 파라미터: size(응답으로 받을 최대 검색 결과 수, Default: 10, Max: 30)
                                        
                    요청 예시: /v1/stores/search?name=김밥&x=123.5156&y=36.1425&size=15
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "가게 검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.검색어가 빈 값이나 공백인 경우
                                                        
                            2.사용자 위치의 경도 값이 숫자가 아닌 경우
                                                        
                            3.사용자 위치의 위도 값이 숫자가 아닌 경우
                                                        
                            4.검색 결과 수가 최대 결과 수(30)보다 큰 경우
                                                        
                            5.검색 결과 수가 0이하인 경우
                                                        
                            6.사용자 위치의 경도 값이 없는 경우
                                                        
                            7.사용자 위치의 위도 값이 없는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )

    })
    ResponseEntity<StoreSearchResponses> search(
            @Parameter(description = "검색 키워드", example = "김밥")
            @NotBlank(message = "검색어는 빈 값이 될 수 없습니다.") String name,

            @Parameter(description = "사용자 경도", example = "126.1245")
            @NotNull(message = "경도는 반드시 포함되어야 합니다.") BigDecimal x,

            @Parameter(description = "사용자 위도", example = "37.1245")
            @NotNull(message = "위도는 반드시 포함되어야 합니다.") BigDecimal y,

            @Parameter(description = "검색 최대 결과 수", example = "15")
            @Positive(message = "조회 크기는 1이상만 가능합니다.")
            @Max(value = 30, message = "최대 30개까지 조회가능합니다.") int size
    );

    @Operation(
            summary = "북마크한 가게 목록 범위 기반 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 북마크한 가게 목록을 조회하는 기능입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "북마크한 가게 목록 범위 기반 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.지도 중심 경도가 존재하지 않은 경우
                                                        
                            2.지도 중심 위도가 존재하지 않은 경우
                                                        
                            3.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            4.지도 경도 증가값이 양수가 아닌 경우
                                                        
                            5.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 위도 증가값이 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<StoreMapBoundResponses> getStoresByBookmarkInMapBounds(
            @Parameter(description = "지도 중심 경도", example = "123.3636")
            BigDecimal x,

            @Parameter(description = "지도 중심 위도", example = "32.3636")
            BigDecimal y,

            @Parameter(description = "지도 경도 증가값", example = "3.1212")
            @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.")
            BigDecimal deltaX,

            @Parameter(description = "지도 위도 증가값", example = "3.1212")
            @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.")
            BigDecimal deltaY,

            Member loginMember
    );

    @Operation(
            summary = "팔로잉 하는 유저의 리뷰가 존재하는 가게 목록 범위 기반 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 팔로잉 하는 유저의 리뷰가 존재하는 가게 목록을 조회하는 기능입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팔로잉 하는 유저의 리뷰가 존재하는 가게 목록 범위 기반 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.지도 중심 경도가 존재하지 않은 경우
                                                        
                            2.지도 중심 위도가 존재하지 않은 경우
                                                        
                            3.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            4.지도 경도 증가값이 양수가 아닌 경우
                                                        
                            5.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 위도 증가값이 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<StoreMapBoundResponses> getStoresByFollowingInMapBounds(
            @Parameter(description = "지도 중심 경도", example = "123.3636")
            BigDecimal x,

            @Parameter(description = "지도 중심 위도", example = "32.3636")
            BigDecimal y,

            @Parameter(description = "지도 경도 증가값", example = "3.1212")
            @Positive(message = "경도 증가값은 0이상의 양수만 가능합니다.")
            BigDecimal deltaX,

            @Parameter(description = "지도 위도 증가값", example = "3.1212")
            @Positive(message = "위도 증가값은 0이상의 양수만 가능합니다.")
            BigDecimal deltaY,

            Member loginMember
    );
}
