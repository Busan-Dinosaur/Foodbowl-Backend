package org.dinosaur.foodbowl.domain.post.application;

import static org.dinosaur.foodbowl.global.exception.ErrorStatus.MEMBER_NOT_FOUND;
import static org.dinosaur.foodbowl.global.exception.ErrorStatus.STORE_NOT_FOUND;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.dinosaur.foodbowl.domain.member.entity.Member;
import org.dinosaur.foodbowl.domain.member.repository.MemberRepository;
import org.dinosaur.foodbowl.domain.photo.PhotoUtils;
import org.dinosaur.foodbowl.domain.photo.ThumbnailSize;
import org.dinosaur.foodbowl.domain.photo.ThumbnailUtils;
import org.dinosaur.foodbowl.domain.photo.entity.Photo;
import org.dinosaur.foodbowl.domain.photo.entity.Thumbnail;
import org.dinosaur.foodbowl.domain.photo.repository.PhotoRepository;
import org.dinosaur.foodbowl.domain.post.dto.request.PostCreateRequest;
import org.dinosaur.foodbowl.domain.post.dto.response.PostThumbnailResponse;
import org.dinosaur.foodbowl.domain.post.entity.Category;
import org.dinosaur.foodbowl.domain.post.entity.Post;
import org.dinosaur.foodbowl.domain.post.entity.PostCategory;
import org.dinosaur.foodbowl.domain.post.repository.PostCategoryRepository;
import org.dinosaur.foodbowl.domain.post.repository.PostRepository;
import org.dinosaur.foodbowl.domain.store.entity.Store;
import org.dinosaur.foodbowl.domain.store.repository.StoreRepository;
import org.dinosaur.foodbowl.global.dto.PageResponse;
import org.dinosaur.foodbowl.global.exception.FoodbowlException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private static final int THUMBNAIL_INDEX = 0;

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final PostCategoryRepository postCategoryRepository;
    private final PhotoRepository photoRepository;

    private final ThumbnailUtils thumbnailUtils;
    private final PhotoUtils photoUtils;

    @Transactional
    public Long save(final Long memberId, final PostCreateRequest request, final List<MultipartFile> imageFiles) {
        Post post = makePost(memberId, request, imageFiles);
        postRepository.save(post);

        saveCategories(request.getCategoryNames(), post);
        savePhotos(imageFiles, post);
        return post.getId();
    }

    private Post makePost(final Long memberId, final PostCreateRequest request, final List<MultipartFile> imageFiles) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new FoodbowlException(STORE_NOT_FOUND));
        String content = request.getContent();
        Thumbnail thumbnail = makeThumbnail(imageFiles);

        return Post.builder()
                .member(member)
                .thumbnail(thumbnail)
                .store(store)
                .content(content)
                .build();
    }

    private Thumbnail makeThumbnail(final List<MultipartFile> imageFiles) {
        MultipartFile thumbnailFile = imageFiles.get(THUMBNAIL_INDEX);
        ThumbnailSize thumbnailSize = ThumbnailSize.of(thumbnailFile);
        String thumbnailPath = thumbnailUtils.storeImageFile(thumbnailFile);

        return Thumbnail.builder()
                .path(thumbnailPath)
                .width(thumbnailSize.width())
                .height(thumbnailSize.height())
                .build();
    }

    private void saveCategories(final List<String> categoryNames, final Post post) {
        List<PostCategory> postCategories = categoryNames.stream()
                .map(Category::from)
                .map(category -> PostCategory.builder().post(post).category(category).build())
                .toList();
        postCategoryRepository.saveAll(postCategories);
    }

    private void savePhotos(final List<MultipartFile> imageFiles, final Post post) {
        List<String> photoPaths = photoUtils.storeImageFiles(imageFiles);
        List<Photo> photos = photoPaths.stream()
                .map(photoPath -> Photo.builder().path(photoPath).post(post).build())
                .toList();
        photoRepository.saveAll(photos);
    }

    public PageResponse<PostThumbnailResponse> findThumbnailsInProfile(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new FoodbowlException(MEMBER_NOT_FOUND));

        Page<PostThumbnailResponse> pageOfResponse = postRepository.findAllByMember(member, pageable)
                .map(PostThumbnailResponse::from);
        return PageResponse.from(pageOfResponse);
    }
}
