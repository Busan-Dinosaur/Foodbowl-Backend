package org.dinosaur.foodbowl.domain.member.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.persistence.MemberCustomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberCustomService {

    private final MemberCustomRepository memberCustomRepository;

    @Transactional(readOnly = true)
    public List<Member> search(String name, int size) {
        return memberCustomRepository.search(name, size);
    }

    @Transactional(readOnly = true)
    public List<Member> getMembersSortByReviewCounts(int page, int size) {
        return memberCustomRepository.getMembersSortByReviewCounts(page, size);
    }
}
