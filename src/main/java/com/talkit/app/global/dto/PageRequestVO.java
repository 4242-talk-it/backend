package com.talkit.app.global.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public record PageRequestVO(int pageNo, int pageSize) {
    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 30;

    public PageRequestVO(int pageNo, int pageSize) {
        this.pageNo = validatePageNo(pageNo);
        this.pageSize = validatePageSize(pageSize);
    }

    public static PageRequestVO of(Integer pageNo, Integer pageSize) {
        return new PageRequestVO(
            Optional.ofNullable(pageNo).orElse(DEFAULT_PAGE_NO),
            Optional.ofNullable(pageSize).map(PageRequestVO::validatePageSize).orElse(DEFAULT_PAGE_SIZE)
        );
    }

    public Pageable toPageable() {
        return PageRequest.of(pageNo - 1, pageSize);
    }

    private static int validatePageNo(int pageNo) {
        return pageNo >= 1 ? pageNo : DEFAULT_PAGE_NO;
    }

    private static int validatePageSize(int pageSize) {
        if (pageSize < 1) return DEFAULT_PAGE_SIZE;
        if (pageSize > MAX_PAGE_SIZE) return MAX_PAGE_SIZE;
        return pageSize;
    }
}
