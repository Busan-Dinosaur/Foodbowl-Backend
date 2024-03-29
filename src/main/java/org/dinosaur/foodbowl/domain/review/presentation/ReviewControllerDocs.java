package org.dinosaur.foodbowl.domain.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewCreateRequest;
import org.dinosaur.foodbowl.domain.review.dto.request.ReviewUpdateRequest;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewFeedPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewPageResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.ReviewResponse;
import org.dinosaur.foodbowl.domain.review.dto.response.StoreReviewResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "리뷰", description = "리뷰 API")
public interface ReviewControllerDocs {

    @Operation(
            summary = "단건 리뷰 조회",
            description = """
                    단건 리뷰를 조회합니다.
                                        
                    리뷰 작성자, 리뷰 본문, 리뷰 가게 정보를 응답으로 반환합니다.
                                        
                    리뷰 목록 조회 결과에서 리뷰 하나의 응답 데이터와 동일합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "단건 리뷰 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID가 양수가 아닌 경우
                                                        
                            2.디바이스 경도가 존재하지 않는 경우
                                                        
                            3.디바이스 위도가 존재하지 않는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            1.일치하는 리뷰가 존재하지 않는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewResponse> getReview(
            @Parameter(description = "리뷰 ID", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long reviewId,

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            LoginMember loginMember
    );

    @Operation(
            summary = "위도, 경도 범위 기반 리뷰 목록 페이징 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 가게에 작성된 리뷰 목록을 조회하는 기능입니다.
                                        
                    카테고리를 이용해 필터링 조건을 추가할 수 있습니다.
                                        
                    모든 카테고리를 조회하는 경우 파라미터 조건을 추가하지 않으면 됩니다.
                                        
                    모든 카테고리 예시: /v1/reviews/bounds
                                        
                    카테고리 필터링 요청 예시: /v1/reviews/bounds?category=한식
                                        
                    가능한 카테고리 필터는 다음과 같습니다. 이외의 카테고리를 요청에 보낼 시 400 응답을 반환합니다.
                                        
                    카페, 술집, 한식, 양식, 일식, 중식, 치킨, 분식, 해산물, 샐러드, 기타
                                        
                    디바이스 경도(deviceX), 디바이스 위도(deviceY)를 통해 디바이스와 가게 사이의 거리를 계산합니다.
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 목록 범위 기반 페이징 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID가 양수가 아닌 경우
                                                        
                            2.지도 중심 경도가 존재하지 않은 경우
                                                        
                            3.지도 중심 위도가 존재하지 않은 경우
                                                        
                            4.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            5.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 0이상 양수가 아닌 경우
                                                        
                            7.지도 위도 증가값이 0이상 양수가 아닌 경우
                                                        
                            8.디바이스 경도가 존재하지 않은 경우
                                                        
                            9.디바이스 위도가 존재하지 않은 경우
                                                        
                            10.페이지 크기가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewPageResponse> getReviewsInMapBounds(
            @Parameter(description = "이전 조회의 마지막 리뷰 ID(첫 조회 시에는 파라미터 요청 X)", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

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

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            @Parameter(description = "카테고리", example = "분식")
            Optional<String> category,

            LoginMember loginMember
    );

    @Operation(
            summary = "리뷰 피드 조회",
            description = """
                    리뷰를 피드를 조회합니다.
                                        
                    피드에서 조회되는 리뷰는, 사진이 포함된 리뷰만 조회됩니다.
                                        
                    기존 리뷰 목록 조회와 동일한 응답에 리뷰 썸네일 필드(reviewFeedThumbnail)를 추가했습니다.
                                        
                    피드 조회 결과는 최신 피드(리뷰) 순서대로 반환합니다.
                                        
                    기존 페이징 조회와 다르게, 피드 조회 페이징의 pageSize default 값은 20입니다.
                                        
                    피드 화면에서 기본으로 15개 이상의 리뷰가 한번에 보이기 때문에 20으로 설정했습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 피드 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID가 양수가 아닌 경우
                                                        
                            2.디바이스 경도가 존재하지 않는 경우
                                                        
                            3.디바이스 위도가 존재하지 않는 경우
                                                        
                            4.페이지 크기가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewFeedPageResponse> getReviewFeeds(
            @Parameter(description = "리뷰 ID", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            LoginMember loginMember
    );

    @Operation(
            summary = "멤버의 리뷰 목록 범위 기반 페이징 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 멤버의 리뷰 목록을 조회하는 기능입니다.
                                        
                    카테고리를 이용해 필터링 조건을 추가할 수 있습니다.
                                        
                    모든 카테고리를 조회하는 경우 파라미터 조건을 추가하지 않으면 됩니다.
                                        
                    모든 카테고리 예시: /v1/reviews/members
                                        
                    카테고리 필터링 요청 예시: /v1/reviews/members?category=한식
                                        
                    가능한 카테고리 필터는 다음과 같습니다. 이외의 카테고리를 요청에 보낼 시 400 응답을 반환합니다.
                                        
                    카페, 술집, 한식, 양식, 일식, 중식, 치킨, 분식, 해산물, 샐러드, 기타
                                        
                    디바이스 경도(deviceX), 디바이스 위도(deviceY)를 통해 디바이스와 가게 사이의 거리를 계산합니다.
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "멤버의 리뷰 목록 범위 기반 페이징 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.멤버 ID가 존재하지 않은 경우
                                                        
                            2.멤버 ID가 양수가 아닌 경우
                                                        
                            3.리뷰 ID가 양수가 아닌 경우
                                                        
                            4.지도 중심 경도가 존재하지 않은 경우
                                                        
                            5.지도 중심 위도가 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            7.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            8.지도 경도 증가값이 0이상 양수가 아닌 경우
                                                        
                            9.지도 위도 증가값이 0이상 양수가 아닌 경우
                                                        
                            10.디바이스 경도가 존재하지 않은 경우
                                                        
                            11.디바이스 위도가 존재하지 않은 경우
                                                        
                            12.페이지 크기가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewPageResponse> getReviewsByMemberInMapBounds(
            @Parameter(description = "리뷰 조회 멤버 ID", example = "1")
            @Positive(message = "멤버 ID는 양수만 가능합니다.")
            Long memberId,

            @Parameter(description = "이전 조회의 마지막 리뷰 ID(첫 조회 시에는 파라미터 요청 X)", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

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

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            @Parameter(description = "카테고리", example = "분식")
            Optional<String> category,

            LoginMember loginMember
    );

    @Operation(
            summary = "가게 리뷰 필터링 페이징 조회",
            description = """
                    특정 가게에 해당하는 리뷰를 조회합니다.
                                        
                    가게에 해당하는 '모든' 리뷰를 조회하거나, 필터링 조건을 사용해 '친구들' 리뷰만 모아볼 수 있습니다.
                                        
                    요청 예시: /v1/reviews/stores?filter=FRIEND
                                        
                    검색 필터링 요청 파라미터: filter (필수 값이 아닙니다. 해당 파라미터 없이 요청 시 모든 결과가 반환됩니다.)
                                        
                    서버에서 허용하는 요청 파라미터 값 : ALL(모든), FRIEND(친구만)
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해당 가게에 해당하는 리뷰 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.가게 ID가 양수가 아닌 경우
                                                        
                            2.가게 ID가 존재하지 않는 경우
                                                        
                            3.페이지 크기가 양수가 아닌 경우
                                                        
                            4.마지막 리뷰 ID가 양수가 아닌 경우
                                                        
                            5.일치하는 필터링 조건이 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 가게인 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<StoreReviewResponse> getReviewsByStore(
            @Parameter(description = "가게 ID", example = "1")
            @Positive(message = "가게 ID는 양수만 가능합니다.")
            Long storeId,

            @Parameter(description = "리뷰 필터", example = "FRIEND")
            String filter,

            @Parameter(description = "이전 조회의 마지막 리뷰 ID(첫 조회 시에는 파라미터 요청 X)", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            LoginMember loginMember
    );

    @Operation(
            summary = "북마크한 가게의 리뷰 목록 범위 기반 페이징 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 북마크 한 가게에 작성된 리뷰 목록을 조회하는 기능입니다.
                                        
                    디바이스 경도(deviceX), 디바이스 위도(deviceY)를 통해 디바이스와 가게 사이의 거리를 계산합니다.
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "북마크한 가게의 리뷰 목록 범위 기반 페이징 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID가 양수가 아닌 경우
                                                        
                            2.지도 중심 경도가 존재하지 않은 경우
                                                        
                            3.지도 중심 위도가 존재하지 않은 경우
                                                        
                            4.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            5.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 0이상 양수가 아닌 경우
                                                        
                            7.지도 위도 증가값이 0이상 양수가 아닌 경우
                                                        
                            8.디바이스 경도가 존재하지 않은 경우
                                                        
                            9.디바이스 위도가 존재하지 않은 경우
                                                        
                            10.페이지 크기가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewPageResponse> getReviewsByBookmarkInMapBounds(
            @Parameter(description = "이전 조회의 마지막 리뷰 ID(첫 조회 시에는 파라미터 요청 X)", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

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

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            LoginMember loginMember
    );

    @Operation(
            summary = "팔로잉 멤버의 리뷰 목록 범위 기반 페이징 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 팔로잉 하고 있는 멤버들이 작성한 리뷰 목록을 조회하는 기능입니다.
                                        
                    카테고리를 이용해 필터링 조건을 추가할 수 있습니다.
                                        
                    모든 카테고리를 조회하는 경우 파라미터 조건을 추가하지 않으면 됩니다.
                                        
                    모든 카테고리 예시: /v1/reviews/following
                                        
                    카테고리 필터링 요청 예시: /v1/reviews/following?category=한식
                                        
                    가능한 카테고리 필터는 다음과 같습니다. 이외의 카테고리를 요청에 보낼 시 400 응답을 반환합니다.
                                        
                    카페, 술집, 한식, 양식, 일식, 중식, 치킨, 분식, 해산물, 샐러드, 기타
                                        
                    디바이스 경도(deviceX), 디바이스 위도(deviceY)를 통해 디바이스와 가게 사이의 거리를 계산합니다.
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "팔로잉 멤버의 리뷰 목록 범위 기반 페이징 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID가 양수가 아닌 경우
                                                        
                            2.지도 중심 경도가 존재하지 않은 경우
                                                        
                            3.지도 중심 위도가 존재하지 않은 경우
                                                        
                            4.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            5.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 0이상 양수가 아닌 경우
                                                        
                            7.지도 위도 증가값이 0이상 양수가 아닌 경우
                                                        
                            8.디바이스 경도가 존재하지 않은 경우
                                                        
                            9.디바이스 위도가 존재하지 않은 경우
                                                        
                            10.페이지 크기가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewPageResponse> getReviewsByFollowingInMapBounds(
            @Parameter(description = "이전 조회의 마지막 리뷰 ID(첫 조회 시에는 파라미터 요청 X)", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

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

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            @Parameter(description = "카테고리", example = "치킨")
            Optional<String> category,

            LoginMember loginMember
    );

    @Operation(
            summary = "학교 근처 가게 리뷰 목록 범위 기반 페이징 조회",
            description = """
                    지도 중심의 경도(x), 위도(y)와 경도 증가값(deltaX), 위도 증가값(deltaY)을 통해 사각형 범위를 생성하여
                                        
                    해당 범위에 속한 학교 근처 가게의 리뷰 목록을 조회하는 기능입니다.
                                        
                    카테고리를 이용해 필터링 조건을 추가할 수 있습니다.
                                        
                    모든 카테고리를 조회하는 경우 파라미터 조건을 추가하지 않으면 됩니다.
                                        
                    모든 카테고리 예시: /v1/reviews/schools
                                        
                    카테고리 필터링 요청 예시: /v1/reviews/schools?category=한식
                                        
                    가능한 카테고리 필터는 다음과 같습니다. 이외의 카테고리를 요청에 보낼 시 400 응답을 반환합니다.
                                        
                    카페, 술집, 한식, 양식, 일식, 중식, 치킨, 분식, 해산물, 샐러드, 기타
                                        
                    디바이스 경도(deviceX), 디바이스 위도(deviceY)를 통해 디바이스와 가게 사이의 거리를 계산합니다.
                                        
                    조회 성능을 높이기 위해 NO OFFSET 페이징으로 구현하였기에 페이지 번호 대신 마지막 리뷰 ID를 요청값으로 받습니다.
                                        
                    첫 페이지 조회 시에는 마지막 리뷰 ID를 파라미터에 담지 않고 요청을 보내면 됩니다.
                                        
                    두번째 페이지 조회 부터는 이전 조회 응답에 담겨 있는 마지막 리뷰 ID를 파라미터로 넘겨주면
                                        
                    해당 리뷰 ID보다 작은, 다시 말해서 요청으로 보낸 리뷰 이전에 작성된 리뷰부터 조회하게 됩니다.
                                        
                    페이지 크기(응답할 리뷰 개수)는 파라미터로 보내지 않으면 기본적으로 10개로 동작하게 되어있습니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "학교 근처 가게 리뷰 목록 범위 기반 페이징 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.학교 ID가 존재하지 않은 경우
                                                        
                            2.학교 ID가 양수가 아닌 경우
                                                        
                            3.리뷰 ID가 양수가 아닌 경우
                                                        
                            4.지도 중심 경도가 존재하지 않은 경우
                                                        
                            5.지도 중심 위도가 존재하지 않은 경우
                                                        
                            6.지도 경도 증가값이 존재하지 않은 경우
                                                        
                            7.지도 위도 증가값이 존재하지 않은 경우
                                                        
                            8.지도 경도 증가값이 0이상 양수가 아닌 경우
                                                        
                            9.지도 위도 증가값이 0이상 양수가 아닌 경우
                                                        
                            10.디바이스 경도가 존재하지 않은 경우
                                                        
                            11.디바이스 위도가 존재하지 않은 경우
                                                        
                            12.페이지 크기가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "학교가 존재하지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<ReviewPageResponse> getReviewsBySchoolInMapBounds(
            @Parameter(description = "학교 ID", example = "1")
            @Positive(message = "학교 ID는 양수만 가능합니다.")
            Long schoolId,

            @Parameter(description = "이전 조회의 마지막 리뷰 ID(첫 조회 시에는 파라미터 요청 X)", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long lastReviewId,

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

            @Parameter(description = "사용자 경도", example = "123.3636")
            BigDecimal deviceX,

            @Parameter(description = "사용자 위도", example = "32.3636")
            BigDecimal deviceY,

            @Parameter(description = "페이지 크기", example = "10")
            @Positive(message = "페이지 크기는 양수만 가능합니다.")
            int pageSize,

            @Parameter(description = "카테고리", example = "카페")
            Optional<String> category,

            LoginMember loginMember
    );

    @Operation(summary = "리뷰 등록", description = "가게에 해당하는 리뷰를 등록합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.사진이 4개보다 많은 경우
                                                        
                            2.가게 이름 값이 없는 경우
                                                        
                            3.가게 주소 필드가 값이 없는 경우
                                                        
                            4.가게 경도 값이 없는 경우
                                                        
                            5.가게 위도 값이 없는 경우
                                                        
                            6.가게 정보 URL이 없는 경우
                                                        
                            7.카테고리 정보가 없는 경우
                                                        
                            8.가게 리뷰 내용이 없는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> create(
            @Valid ReviewCreateRequest reviewCreateRequest,
            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,
            LoginMember loginMember
    );

    @Operation(summary = "리뷰 수정",
            description = """
                    가게에 해당하는 리뷰를 수정합니다.
                                            
                    images 필드에는 새롭게 추가되는 사진을 보내면 됩니다.
                                            
                    request의 deleteIds 필드에는 삭제하는 사진의 ID를 담아서 보내면 됩니다.
                                            
                    삭제하는 사진이 없는 경우에도 deleteIds 빈 배열 '[]'을 반드시 보내야 합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "리뷰 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.사진이 4개보다 많은 경우
                                                        
                            2.삭제 사진 필드가 없는 경우
                                                        
                            3.삭제 사진 ID에 음수가 포함된 경우
                                                        
                            4.수정하는 리뷰 내용이 없는 경우
                                                        
                            5.양수가 아닌 리뷰 ID
                                                        
                            6.삭제 사진 ID가 4개보다 많은 경우
                                                        
                            7.삭제하려는 사진이 해당 리뷰의 사진이 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리뷰가 존재하지 않을 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> update(
            @Parameter(description = "리뷰 ID", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.") Long reviewId,

            @Valid ReviewUpdateRequest reviewUpdateRequest,

            @Size(max = 4, message = "사진의 개수는 최대 4개까지 가능합니다.") List<MultipartFile> imageFiles,

            LoginMember loginMember
    );

    @Operation(summary = "리뷰 삭제", description = "사용자가 작성한 리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "리뷰 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.리뷰 ID 타입이 올바르지 않은 경우
                                                        
                            2.리뷰 ID가 양수가 아닌 경우
                                                        
                            3.리뷰 작성자와 요청을 보낸 사용자가 다른 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "리뷰가 존재하지 않을 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> delete(
            @Parameter(description = "리뷰 ID", example = "1")
            @Positive(message = "리뷰 ID는 양수만 가능합니다.")
            Long reviewId,

            LoginMember loginMember
    );
}
