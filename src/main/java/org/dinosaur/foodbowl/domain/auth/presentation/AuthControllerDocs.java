package org.dinosaur.foodbowl.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.AppleLoginRequest;
import org.dinosaur.foodbowl.domain.auth.dto.reqeust.RenewTokenRequest;
import org.dinosaur.foodbowl.domain.auth.dto.response.TokenResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증", description = "인증 API")
public interface AuthControllerDocs {

    @Operation(summary = "애플 로그인", description = "애플 로그인/회원가입을 진행한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "애플 로그인/회원가입 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 애플 토큰",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "유효하지 않은 애플 키",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<TokenResponse> appleLogin(AppleLoginRequest appleLoginRequest);

    @Operation(summary = "인증 토큰 갱신", description = "갱신 토큰을 통해 인증 토큰을 갱신한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "인증 토큰 갱신 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.인증 토큰이 존재하지 않거나 공백인 경우
                                                        
                            2.갱신 토큰이 존재하지 않거나 공백인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            1.인증 토큰이 유효하지 않은 경우
                                                        
                            2.갱신 토큰이 유효하지 않은 경우
                                                        
                            3.인증 토큰에 알맞은 갱신 토큰이 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<TokenResponse> renewToken(RenewTokenRequest renewTokenRequest);
}
