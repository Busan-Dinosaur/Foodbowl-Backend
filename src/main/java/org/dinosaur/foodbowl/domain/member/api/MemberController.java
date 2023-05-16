package org.dinosaur.foodbowl.domain.member.api;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameDuplicateCheckResponse;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/check-nickname")
    public ResponseEntity<NicknameDuplicateCheckResponse> checkDuplicate(
            @Pattern(regexp = "^[a-zA-Z가-힣0-9]{1,16}$", message = "닉네임은 1자 이상 16자 이하 한글,영문,숫자만 가능합니다")
            @RequestParam String nickname
    ) {
        return ResponseEntity.ok(memberService.checkDuplicate(nickname));
    }
  
    @DeleteMapping
    public ResponseEntity<Void> withDraw(@MemberId Long memberId) {
        memberService.withDraw(memberId);
        return ResponseEntity.noContent().build();
    }
}
