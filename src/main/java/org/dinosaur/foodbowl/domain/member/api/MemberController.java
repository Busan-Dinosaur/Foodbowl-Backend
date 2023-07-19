package org.dinosaur.foodbowl.domain.member.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.application.MemberService;
import org.dinosaur.foodbowl.domain.member.dto.request.ProfileUpdateRequest;
import org.dinosaur.foodbowl.domain.member.dto.response.MemberProfileResponse;
import org.dinosaur.foodbowl.resolver.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{id}/profile")
    public ResponseEntity<MemberProfileResponse> getMemberProfile(
            @PathVariable(name = "id") Long profileMemberId,
            @MemberId Long memberId
    ) {
        MemberProfileResponse response = memberService.getMemberProfile(profileMemberId, memberId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(
            @MemberId Long memberId,
            @Valid @RequestBody ProfileUpdateRequest profileUpdateRequest
    ) {
        memberService.updateProfile(memberId, profileUpdateRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> withDraw(@MemberId Long memberId) {
        memberService.withDraw(memberId);
        return ResponseEntity.noContent().build();
    }
}
