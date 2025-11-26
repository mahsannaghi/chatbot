package com.paya.EncouragementService.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageableResponse<T> {

    @Getter
    @Setter
    private class Pagination {
        int totalPages;
        long totalElements;
        int size;
        int currentPage;

        public Pagination(Page<T> page) {
            this.totalPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.size = page.getSize();
            this.currentPage = page.getNumber();
        }

    }

    private Pagination pagination;
    private List<T> content;

    public PageableResponse(Page<T> page) {

        this.pagination = new Pagination(page);
        this.content = page.getContent();
    }
}