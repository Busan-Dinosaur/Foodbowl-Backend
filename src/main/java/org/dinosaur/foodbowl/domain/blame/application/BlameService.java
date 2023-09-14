package org.dinosaur.foodbowl.domain.blame.application;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.blame.domain.Blame;
import org.dinosaur.foodbowl.domain.blame.domain.vo.BlameTarget;
import org.dinosaur.foodbowl.domain.blame.dto.request.BlameRequest;
import org.dinosaur.foodbowl.domain.blame.exception.BlameExceptionType;
import org.dinosaur.foodbowl.domain.blame.persistence.BlameRepository;
import org.dinosaur.foodbowl.domain.member.domain.Member;
import org.dinosaur.foodbowl.domain.member.persistence.MemberRepository;
import org.dinosaur.foodbowl.domain.review.persistence.ReviewRepository;
import org.dinosaur.foodbowl.global.exception.BadRequestException;
import org.dinosaur.foodbowl.global.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BlameService {

    private final BlameRepository blameRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    private final Map<BlameTarget, Predicate<Long>> reportCheck = new EnumMap<>(BlameTarget.class) {{
        put(BlameTarget.MEMBER, BlameService.this::existMember);
        put(BlameTarget.REVIEW, BlameService.this::existReview);
    }};

    private final Map<BlameTarget, BiPredicate<Long, Member>> reportMe = new EnumMap<>(BlameTarget.class) {{
        put(BlameTarget.MEMBER, BlameService.this::isMemberMe);
        put(BlameTarget.REVIEW, BlameService.this::isReviewMe);
    }};

    private boolean existMember(Long targetId) {
        return memberRepository.findById(targetId).isPresent();
    }

    private boolean existReview(Long targetId) {
        return reviewRepository.findById(targetId).isPresent();
    }

    private boolean isMemberMe(Long targetId, Member member) {
        return Objects.equals(targetId, member.getId());
    }

    private boolean isReviewMe(Long targetId, Member member) {
        return reviewRepository.findById(targetId)
                .map(review -> !review.isNotOwnerOf(member))
                .orElse(false);
    }

    @Transactional
    public void blame(BlameRequest blameRequest, Member loginMember) {
        validateBlame(blameRequest, loginMember);
        Blame blame = Blame.builder()
                .member(loginMember)
                .targetId(blameRequest.targetId())
                .blameTarget(blameRequest.blameTarget())
                .description(blameRequest.description())
                .build();
        blameRepository.save(blame);
    }

    private void validateBlame(BlameRequest blameRequest, Member member) {
        validateExistTarget(blameRequest);
        validateBlameMe(blameRequest, member);
        validateDuplicate(blameRequest, member);
    }

    private void validateExistTarget(BlameRequest blameRequest) {
        boolean existTarget = reportCheck.get(blameRequest.blameTarget()).test(blameRequest.targetId());
        if (!existTarget) {
            throw new NotFoundException(BlameExceptionType.NOT_EXIST_TARGET);
        }
    }

    private void validateBlameMe(BlameRequest blameRequest, Member member) {
        boolean blameMe = reportMe.get(blameRequest.blameTarget()).test(blameRequest.targetId(), member);
        if (blameMe) {
            throw new BadRequestException(BlameExceptionType.BLAME_ME);
        }
    }

    private void validateDuplicate(BlameRequest blameRequest, Member member) {
        Optional<Blame> blame = blameRepository.findByMemberAndTargetIdAndBlameTarget(
                member,
                blameRequest.targetId(),
                blameRequest.blameTarget()
        );
        if (blame.isPresent()) {
            throw new BadRequestException(BlameExceptionType.DUPLICATE_BLAME);
        }
    }
}
