package org.dinosaur.foodbowl.global.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageResponse<T> {

    private List<T> content;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private int currentPage;
    private int currentElementSize;
    private int totalPage;
    private long totalElementSize;

    public static <T> PageResponse<T> from(final Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
