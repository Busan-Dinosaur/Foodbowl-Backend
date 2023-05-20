package org.dinosaur.foodbowl.domain.member.api;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.global.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @DeleteMapping
    public ResponseEntity<Void> withDraw(@MemberId Long memberId) {
        memberService.withDraw(memberId);
        return ResponseEntity.noContent().build();
    }
}
