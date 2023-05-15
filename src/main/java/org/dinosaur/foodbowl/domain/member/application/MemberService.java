package org.dinosaur.foodbowl.domain.member.application;

import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.dto.response.NicknameDuplicateCheckResponse;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public NicknameDuplicateCheckResponse checkDuplicate(String nickname) {
        boolean hasDuplicate = memberRepository.existsByNickname(nickname);
        return new NicknameDuplicateCheckResponse(hasDuplicate);
    }
}
