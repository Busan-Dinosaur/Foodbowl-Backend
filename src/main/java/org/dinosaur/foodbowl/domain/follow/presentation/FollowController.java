package org.dinosaur.foodbowl.domain.follow.presentation;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.application.FollowService;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/follows")
@RestController
public class FollowController implements FollowControllerDocs {

    private final FollowService followService;

    @PostMapping("/{memberId}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable(name = "memberId") Long targetMemberId,
            @Auth Member loginMember
    ) {
        followService.follow(targetMemberId, loginMember);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}/unfollow")
    public ResponseEntity<Void> unfollow(
            @PathVariable(name = "memberId") Long targetMemberId,
            @Auth Member loginMember
    ) {
        followService.unfollow(targetMemberId, loginMember);
        return ResponseEntity.noContent().build();
    }
}
