package org.dinosaur.foodbowl.domain.member.presentation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameExistResponse;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/nickname/exist")
    public ResponseEntity<NicknameExistResponse> checkNicknameExist(
            @RequestParam @NotBlank(message = "닉네임이 존재하지 않습니다.") String nickname
    ) {
        NicknameExistResponse response = memberService.checkNicknameExist(nickname);
        return ResponseEntity.ok(response);
    }
}
