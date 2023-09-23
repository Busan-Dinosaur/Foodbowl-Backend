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
import org.springframework.web.multipart.MultipartFile;

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
                            1.회원 ID 타입이 올바르지 않은 경우
                                                        
                            2.회원 ID가 양수가 아닌 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 ID가 등록되지 않은 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<MemberProfileResponse> getProfile(
            @Parameter(description = "회원 ID", example = "1")
            @PositiveOrZero(message = "회원 ID는 양수만 가능합니다.")
            Long memberId,

            Member loginMember
    );

    @Operation(summary = "나의 프로필 조회", description = "나의 프로필을 조회한다.")
    @ApiResponse(
            responseCode = "200",
            description = "나의 프로필 조회 성공"
    )
    ResponseEntity<MemberProfileResponse> getMyProfile(Member loginMember);

    @Operation(summary = "닉네임 존재 여부 확인", description = "닉네임이 존재하는지 여부를 확인한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "닉네임 존재 여부 확인 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.닉네임 파라미터가 존재하지 않는 경우
                                                        
                            2.닉네임 파라미터가 공백인 경우
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
                            1.닉네임이 공백이거나 존재하지 않는 경우
                                                        
                            2.닉네임이 제약사항에 맞지 않는 경우
                                                        
                            3.한 줄 소개가 제약사항에 맞지 않는 경우
                                                        
                            4.이미 존재하는 닉네임인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> updateProfile(@Valid UpdateProfileRequest updateProfileRequest, Member loginMember);

    @Operation(
            summary = "프로필 이미지 수정",
            description = """
                    프로필 이미지를 수정합니다.
                                        
                    프로필이 존재하지 않을 때, thumbnail 필드를 요청에 담지 않으면 프로필이 존재하지 않는 상태를 유지합니다.
                                        
                    프로필이 존재하지 않을 때, thumbnail에 이미지를 담아 요청한다면 해당 이미지로 프로필이 설정됩니다.
                                        
                    프로필이 존재할 때, thumbnail 필드를 요청에 담지 않으면 기존 프로필이 삭제됩니다.
                                        
                    프로필이 존재할 때, thumbnail에 이미지를 담아 요청한다면 해당 이미지로 프로필을 변경합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "프로필 이미지 수정 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.기존 프로필 이미지 삭제에 실패하는 경우
                                                        
                            2.새로운 프로필 이미지 저장에 실패하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> updateThumbnail(MultipartFile thumbnail, Member loginMember);
}
