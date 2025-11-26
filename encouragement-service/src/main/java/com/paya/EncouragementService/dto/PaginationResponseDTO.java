package com.paya.EncouragementService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationResponseDTO<T> {

    private List<T> content;
    private Integer pageSize;
    private Integer pageNumber;
    private Integer totalPages;
    private Integer totalElements;

}
