package com.paya.EncouragementService.utility;

import com.paya.EncouragementService.dto.PaginationResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public class PaginationUtils {

    public static <T> PaginationResponseDTO<T> paginate(List<T> list, Pageable pageable) {
        int totalElements = list.size();
        int pageSize = pageable.getPageSize();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        int startIndex = pageable.getPageNumber() * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalElements);

        List<T> pagedList = (startIndex < totalElements)
                ? list.subList(startIndex, endIndex)
                : Collections.emptyList();

        return PaginationResponseDTO.<T>builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .pageSize(pageSize)
                .pageNumber(pageable.getPageNumber())
                .content(pagedList)
                .build();
    }

}
