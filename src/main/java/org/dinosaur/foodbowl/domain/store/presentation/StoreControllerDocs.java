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
import java.util.Optional;
import org.dinosaur.foodbowl.domain.store.dto.response.CategoriesResponse;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreMapBoundResponses;
import org.dinosaur.foodbowl.domain.store.dto.response.StoreSearchResponses;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
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
            summary = "위도, 경도 범위 내 존재하는 가게 목록 범위 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 리뷰가 존재하는 가게 목록을 조회하는 기능입니다.
                                        
                    카테고리를 이용해 필터링 조건을 추가할 수 있습니다.
                                        
                    모든 카테고리를 조회하는 경우 파라미터 조건을 추가하지 않으면 됩니다.
                                        
                    모든 카테고리 예시: /v1/stores/bounds
                                        
                    카테고리 필터링 요청 예시: /v1/stores/bounds?category=한식
                                        
                    가능한 카테고리 필터는 다음과 같습니다. 이외의 카테고리를 요청에 보낼 시 400 응답을 반환합니다.
                                        
                    카페, 술집, 한식, 양식, 일식, 중식, 치킨, 분식, 해산물, 샐러드, 기타
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰가 존재하는 가게 목록 범위 기반 조회 성공"
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않은 사용자인 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<StoreMapBoundResponses> getStoresInMapBounds(
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

            @Parameter(description = "카테고리", example = "분식")
            Optional<String> category,

            LoginMember loginMember
    );


    @Operation(
            summary = "멤버 리뷰가 존재하는 가게 목록 범위 기반 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 멤버 리뷰가 존재하는 가게 목록을 조회하는 기능입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "멤버 리뷰가 존재하는 가게 목록 범위 기반 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.멤버 ID가 존재하지 않은 경우
                                                        
                            2.멤버 ID가 양수가 아닌 경우
                                                        
                            3.지도 중심 경도가 존재하지 않은 경우
                                                        
                            4.지도 중심 위도가 존재하지 않은 경우
                                                        
                            5.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 양수가 아닌 경우
                                                        
                            7.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            8.지도 위도 증가값이 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버가 존재하지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<StoreMapBoundResponses> getStoresByMemberInMapBounds(
            @Parameter(description = "멤버 ID", example = "1")
            @Positive(message = "멤버 ID는 양수만 가능합니다.")
            Long memberId,

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

            LoginMember loginMember
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

            LoginMember loginMember
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

            LoginMember loginMember
    );

    @Operation(
            summary = "학교 근처 가게 목록 범위 기반 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 학교 근처 가게 목록을 조회하는 기능입니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학교 근처 가게 목록 범위 기반 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.학교 ID가 존재하지 않은 경우
                                                        
                            2.학교 ID가 양수가 아닌 경우
                                                        
                            3.지도 중심 경도가 존재하지 않은 경우
                                                        
                            4.지도 중심 위도가 존재하지 않은 경우
                                                        
                            5.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 양수가 아닌 경우
                                                        
                            7.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            8.지도 위도 증가값이 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "학교가 존재하지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<StoreMapBoundResponses> getStoresBySchoolInMapBounds(
            @Parameter(description = "학교 ID", example = "1")
            @Positive(message = "학교 ID는 양수만 가능합니다.")
            Long schoolId,

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

            LoginMember loginMember
    );
}
