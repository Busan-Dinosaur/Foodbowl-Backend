package org.dinosaur.foodbowl.domain.member.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileImageResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberSearchResponses;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.global.exception.response.ExceptionResponse;
import org.dinosaur.foodbowl.global.presentation.LoginMember;
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

            LoginMember loginMember
    );

    @Operation(summary = "나의 프로필 조회", description = "나의 프로필을 조회한다.")
    @ApiResponse(
            responseCode = "200",
            description = "나의 프로필 조회 성공"
    )
    ResponseEntity<MemberProfileResponse> getMyProfile(LoginMember loginMember);

    @Operation(
            summary = "회원 검색",
            description = """
                    닉네임으로 회원을 검색합니다.
                                        
                    필수 파라미터: name(검색 키워드)
                                        
                    선택 파라미터: size(응답으로 받을 최대 검색 결과 수, Default: 10, Max: 30)
                                        
                    요청 예시: /v1/memberse/search?name=coby1234&size=15
                                        
                    검색하는 사용자가 검색 결과에 포함되는 경우 isMe 필드가 true로 반환됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 검색 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.검색 파라미터(name)가 존재하지 않는 경우
                                                        
                            2.검색 파라미터(name)가 빈 값이거나 공백인 경우
                                                        
                            3.검색 결과 수가 최대 결과 수(30)보다 큰 경우
                                                        
                            4.검색 결과 수가 0이하인 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<MemberSearchResponses> search(
            @Parameter(description = "검색 키워드", example = "김밥")
            @NotBlank(message = "검색어는 빈 값이 될 수 없습니다.") String name,

            @Parameter(description = "검색 최대 결과 수", example = "15")
            @Positive(message = "조회 크기는 1이상만 가능합니다.")
            @Max(value = 50, message = "최대 50개까지 조회가능합니다.") int size,

            LoginMember loginMember
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
    ResponseEntity<Void> updateProfile(@Valid UpdateProfileRequest updateProfileRequest, LoginMember loginMember);

    @Operation(
            summary = "프로필 이미지 수정",
            description = """
                    요청 이미지로 프로필 이미지를 수정합니다.
                                        
                    요청 이미지가 비어있거나 존재하지 않으면 예외가 발생합니다.
                                        
                    기존 프로필 이미지가 존재하지 않을 경우 요청 이미지를 프로필 이미지로 등록합니다.
                                        
                    기존 프로필 이미지가 존재하는 경우 기존 프로필 이미지를 삭제하고 요청 이미지를 프로필 이미지로 등록합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 이미지 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            1.새로운 프로필 이미지가 존재하지 않을 경우
                                                        
                            2.기존 프로필 이미지 삭제에 실패하는 경우
                                                        
                            3.새로운 프로필 이미지 저장에 실패하는 경우
                            """,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<MemberProfileImageResponse> updateProfileImage(
            @Parameter(description = "수정할 프로필 이미지")
            MultipartFile thumbnail,

            LoginMember loginMember
    );

    @Operation(
            summary = "프로필 이미지 삭제",
            description = """
                    설정되어 있는 프로필 이미지를 삭제합니다.
                                        
                    기존 프로필 이미지가 존재하는 경우 프로필 이미지를 삭제합니다.
                                        
                    기존 프로필 이미지가 존재하지 않을 경우 프로필 이미지가 없는 상태를 유지합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "프로필 이미지 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "기존 프로필 이미지 삭제에 실패하는 경우",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
            )
    })
    ResponseEntity<Void> deleteProfileImage(LoginMember loginMember);

    @Operation(
            summary = "회원 탈퇴",
            description = "회원과 연관된 데이터를 모두 삭제한다."
    )
    @ApiResponse(
            responseCode = "204",
            description = "회원 탈퇴 성공"
    )
    ResponseEntity<Void> deactivate(LoginMember loginMember);
}
