package org.dinosaur.foodbowl.domain.blame.application;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
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
        put(BlameTarget.MEMBER, BlameService.this::isExistMember);
        put(BlameTarget.REVIEW, BlameService.this::isExistReview);
    }};

    private final Map<BlameTarget, BiPredicate<Long, Member>> reportMe = new EnumMap<>(BlameTarget.class) {{
        put(BlameTarget.MEMBER, BlameService.this::isMemberMe);
        put(BlameTarget.REVIEW, BlameService.this::isReviewMe);
    }};

    private boolean isExistMember(Long targetId) {
        return memberRepository.findById(targetId).isPresent();
    }

    private boolean isExistReview(Long targetId) {
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
        BlameTarget blameTarget = BlameTarget.from(blameRequest.blameTarget());
        validateBlame(blameTarget, blameRequest.targetId(), loginMember);
        Blame blame = Blame.builder()
                .member(loginMember)
                .targetId(blameRequest.targetId())
                .blameTarget(blameTarget)
                .description(blameRequest.description())
                .build();
        blameRepository.save(blame);
    }

    private void validateBlame(BlameTarget blameTarget, Long targetId, Member member) {
        validateExistTarget(blameTarget, targetId);
        validateBlameMe(blameTarget, targetId, member);
        validateDuplicate(blameTarget, targetId, member);
    }

    private void validateExistTarget(BlameTarget blameTarget, Long targetId) {
        boolean isExistTarget = reportCheck.get(blameTarget)
                .test(targetId);

        if (!isExistTarget) {
            throw new NotFoundException(BlameExceptionType.NOT_EXIST_TARGET);
        }
    }

    private void validateBlameMe(BlameTarget blameTarget, Long targetId, Member member) {
        boolean blameMe = reportMe.get(blameTarget)
                .test(targetId, member);

        if (blameMe) {
            throw new BadRequestException(BlameExceptionType.BLAME_ME);
        }
    }

    private void validateDuplicate(BlameTarget blameTarget, Long targetId, Member member) {
        blameRepository.findByMemberAndTargetIdAndBlameTarget(member, targetId, blameTarget)
                .ifPresent(exist -> {
                    throw new BadRequestException(BlameExceptionType.DUPLICATE_BLAME);
                });
    }
}
