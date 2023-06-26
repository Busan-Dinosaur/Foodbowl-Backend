package org.dinosaur.foodbowl.domain.post.application;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.post.dto.response.PostStoreMarkerResponse;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.repository.PostRepository;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PageResponse<PostThumbnailResponse> findThumbnailsInProfile(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        Page<PostThumbnailResponse> pageOfResponse = postRepository.findAllByMember(member, pageable)
                .map(PostThumbnailResponse::from);
        return PageResponse.from(pageOfResponse);
    }

    public PageResponse<PostThumbnailResponse> findLatestThumbnails(Pageable pageable) {
        Page<PostThumbnailResponse> pageOfResponse = postRepository.findAll(pageable)
                .map(PostThumbnailResponse::from);
        return PageResponse.from(pageOfResponse);
    }

    public List<PostStoreMarkerResponse> findPostStoreMarkers(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        return postRepository.findAllWithStoreByMember(member)
                .stream()
                .map(Post::getStore)
                .distinct()
                .map(PostStoreMarkerResponse::from)
                .toList();
    }
}
