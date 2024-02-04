package org.dinosaur.foodbowl.domain.blame.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.blame.dto.request.BlameRequest;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
import org.springframework.http.ResponseEntity;

@Tag(name = "신고", description = "신고 API")
public interface BlameControllerDocs {

    @Operation(
            summary = "신고 등록",
            description = """
                    신고하는 대상의 ID를 통해 신고를 등록합니다.
                                        
                    현재는 회원(MEMBER), 리뷰(REVIEW)의 신고만 가능합니다.
                                        
                    blameTarget에 회원을 신고하는 경우 MEMBER를, 리뷰를 신고하는 경우 REVIEW값을 등록합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "신고 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.신고 대상 ID가 양의 정수가 아닌 경우
                                                        
                            2.신고 대상 타입이 정상적이지 않은 경우
                                                        
                            3.신고 사유가 존재하지 않을 경우
                                                        
                            4.스스로를 신고하는 경우
                                                        
                            5.이미 신고 대상에 대한 신고 내역이 존재하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            1.존재하지 않는 회원을 신고하는 경우
                                                        
                            2.존재하지 않는 리뷰를 신고하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> blame(BlameRequest blameRequest, LoginMember loginMember);
}
