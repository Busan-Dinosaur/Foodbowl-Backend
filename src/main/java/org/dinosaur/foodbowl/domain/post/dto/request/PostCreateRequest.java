package org.dinosaur.foodbowl.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateRequest {

    @Positive(message = "가게 ID는 음수가 될 수 없습니다.")
    @NotNull(message = "가게 ID는 반드시 포함되어야 합니다.")
    private Long storeId;

    @Size(min = 1, max = 8, message = "카테고리는 최소 1개, 최대 8개까지 선택 가능합니다.")
    private List<String> categoryNames;

    @Size(min = 1, max = 2000, message = "후기는 최소 1자, 최대 2,000자까지 가능합니다.")
    @NotBlank(message = "후기는 반드시 포함되어야 합니다.")
    private String content;
}
