package org.dinosaur.foodbowl.domain.blame.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.application.BlameService;
import org.dinosaur.foodbowl.domain.blame.dto.request.BlameRequest;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.global.presentation.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/blames")
@RestController
public class BlameController {

    private final BlameService blameService;

    @PostMapping
    public ResponseEntity<Void> blame(@RequestBody @Valid BlameRequest blameRequest, @Auth Member loginMember) {
        blameService.blame(blameRequest, loginMember);
        return ResponseEntity.ok().build();
    }
}
