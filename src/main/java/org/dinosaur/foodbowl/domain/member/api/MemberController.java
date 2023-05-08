package org.dinosaur.foodbowl.domain.member.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.response.DuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.dto.request.DuplicationCheckRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/check-nicknames")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicate(@RequestBody @Valid DuplicationCheckRequest request) {
        return ResponseEntity.ok(memberService.checkDuplicate(request.getNickname()));
    }
}
