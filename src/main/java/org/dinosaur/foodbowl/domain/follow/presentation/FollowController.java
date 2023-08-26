package org.dinosaur.foodbowl.domain.follow.presentation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.follow.application.FollowService;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowerResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.FollowingResponse;
import org.dinosaur.foodbowl.domain.follow.dto.response.OtherUserFollowerResponse;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.common.response.PageResponse;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Validated
@RequestMapping("/v1/follows")
@RestController
public class FollowController implements FollowControllerDocs {

    private final FollowService followService;

    @GetMapping("/followings")
    public ResponseEntity<PageResponse<FollowingResponse>> getFollowings(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "페이지는 0이상만 가능합니다.") int page,
            @RequestParam(defaultValue = "15") @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.") int size,
            @Auth Member loginMember
    ) {
        PageResponse<FollowingResponse> response = followService.getFollowings(page, size, loginMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/followers")
    public ResponseEntity<PageResponse<FollowerResponse>> getFollowers(
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "페이지는 0이상만 가능합니다.") int page,
            @RequestParam(defaultValue = "15") @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.") int size,
            @Auth Member loginMember
    ) {
        PageResponse<FollowerResponse> response = followService.getFollowers(page, size, loginMember);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{memberId}/followers")
    public ResponseEntity<PageResponse<OtherUserFollowerResponse>> getOtherUserFollowers(
            @PathVariable("memberId") @Positive(message = "ID는 양수만 가능합니다.") Long targetMemberId,
            @RequestParam(defaultValue = "0") @PositiveOrZero(message = "페이지는 0이상만 가능합니다.") int page,
            @RequestParam(defaultValue = "15") @PositiveOrZero(message = "페이지 크기는 0이상만 가능합니다.") int size,
            @Auth Member loginMember
    ) {
        PageResponse<OtherUserFollowerResponse> response =
                followService.getOtherUserFollowers(targetMemberId, page, size, loginMember);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{memberId}/follow")
    public ResponseEntity<Void> follow(
            @PathVariable(name = "memberId") @Positive(message = "ID는 양수만 가능합니다.") Long targetMemberId,
            @Auth Member loginMember
    ) {
        followService.follow(targetMemberId, loginMember);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}/unfollow")
    public ResponseEntity<Void> unfollow(
            @PathVariable(name = "memberId") @Positive(message = "ID는 양수만 가능합니다.") Long targetMemberId,
            @Auth Member loginMember
    ) {
        followService.unfollow(targetMemberId, loginMember);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/followers/{memberId}")
    public ResponseEntity<Void> deleteFollower(
            @PathVariable(name = "memberId") @Positive(message = "ID는 양수만 가능합니다.") Long targetMemberId,
            @Auth Member loginMember
    ) {
        followService.deleteFollower(targetMemberId, loginMember);
        return ResponseEntity.noContent().build();
    }
}
