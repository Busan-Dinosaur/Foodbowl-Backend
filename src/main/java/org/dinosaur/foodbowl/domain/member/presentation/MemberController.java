package org.dinosaur.foodbowl.domain.member.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.dto.request.UpdateProfileRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@RestController
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @GetMapping("/{id}/profile")
    public ResponseEntity<MemberProfileResponse> getProfile(
            @PathVariable("id") @PositiveOrZero(message = "회원 ID는 양수만 가능합니다.") Long memberId,
            @Auth Member loginMember
    ) {
        MemberProfileResponse response = memberService.getProfile(memberId, loginMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/profile")
    public ResponseEntity<MemberProfileResponse> getMyProfile(@Auth Member loginMember) {
        MemberProfileResponse response = memberService.getMyProfile(loginMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nickname/exist")
    public ResponseEntity<NicknameExistResponse> checkNicknameExist(
            @RequestParam @NotBlank(message = "닉네임 파라미터 값이 존재하지 않습니다.") String nickname
    ) {
        NicknameExistResponse response = memberService.checkNicknameExist(nickname);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid UpdateProfileRequest updateProfileRequest,
            @Auth Member loginMember
    ) {
        memberService.updateProfile(updateProfileRequest, loginMember);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(value = "/thumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateThumbnail(
            @RequestPart(name = "image", required = false) MultipartFile thumbnail,
            @Auth Member loginMember
    ) {
        memberService.updateThumbnail(thumbnail, loginMember);
        return ResponseEntity.noContent().build();
    }
}
