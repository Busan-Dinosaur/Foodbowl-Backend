package org.dinosaur.foodbowl.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.dto.DuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public DuplicateCheckResponse checkDuplicate(String nickname) {
        boolean isDuplicate = memberRepository.findByNickname(nickname).isPresent();
        return new DuplicateCheckResponse(isDuplicate);
    }
}
