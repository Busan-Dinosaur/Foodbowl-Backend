package org.dinosaur.foodbowl.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.global.exception.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "애플 로그인", description = "애플 로그인/회원가입을 진행한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            1.애플 로그인/회원가입 성공
                            """
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.유효하지 않은 애플 토큰
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            1.유효하지 않은 애플 키
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<TokenResponse> appleLogin(AppleLoginRequest appleLoginRequest);
}
