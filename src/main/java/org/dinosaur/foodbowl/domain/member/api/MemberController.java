package org.dinosaur.foodbowl.domain.member.api;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.DuplicateCheckResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/check-nicknames")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicate(@RequestParam String nickname) {
        return ResponseEntity.ok(memberService.checkDuplicate(nickname));
    }
}
