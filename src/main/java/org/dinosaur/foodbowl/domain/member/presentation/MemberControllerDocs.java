package org.dinosaur.foodbowl.domain.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "회원", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원 프로필 조회", description = "회원 프로필을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 프로필 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.올바르지 않은 회원 ID 타입
                                                        
                            2.양수가 아닌 회원 ID
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "등록되지 않은 회원 ID",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<MemberProfileResponse> getProfile(
            @Parameter(description = "회원 ID", example = "1")
            @PositiveOrZero(message = "회원 ID는 양수만 가능합니다.")
            Long memberId,

            Member loginMember
    );

    @Operation(summary = "닉네임 존재 여부 확인", description = "닉네임이 존재하는지 여부를 확인한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "닉네임 존재 여부 확인 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.존재하지 않는 닉네임 파라미터
                                                        
                            2.공백 닉네임 파라미터
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<NicknameExistResponse> checkNicknameExist(
            @Parameter(description = "닉네임", example = "coby5502")
            @NotBlank(message = "닉네임 파라미터 값이 존재하지 않습니다.")
            String nickname
    );

    @Operation(summary = "프로필 정보 수정", description = "닉네임, 한 줄 소개를 수정한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 정보 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.공백이거나 존재하지 않는 닉네임 요청
                                                        
                            2.공백이거나 존재하지 않는 한 줄 소개 요청
                                                        
                            3.제약사항에 맞지 않는 닉네임
                                                        
                            4.제약사항에 맞지 않는 한 줄 소개
                                                        
                            5.이미 존재하는 닉네임
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> updateProfile(@Valid UpdateProfileRequest updateProfileRequest, Member loginMember);
}
