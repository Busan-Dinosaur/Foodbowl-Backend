package org.dinosaur.foodbowl.domain.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.exception.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "북마크", description = "북마크 API")
public interface BookmarkControllerDocs {

    @Operation(summary = "북마크 추가", description = "가게를 사용자의 북마크에 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 추가 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.가게 ID가 음수인 경우\n
                            2.가게 ID를 숫자로 변환할 수 없는 경우\n
                            3.사용자가 이미 해당 가게를 북마크 등록한 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            1.ID에 해당하는 가게가 존재하지 않는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> create(
            @Positive(message = "가게 ID는 음수가 될 수 없습니다.") Long storeId,
            Member member
    );
}
