package com.kakaotechbootcamp.community.application.common.dto.request;

import com.kakaotechbootcamp.community.application.common.constant.PaginationConstants;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * 페이징 및 정렬을 위한 공통 요청 DTO
 */
public record PageSortRequest(
        Integer page,
        Integer size,
        List<String> sort
) {
    public PageSortRequest {
        page = (page != null && page >= 0) ? page : PaginationConstants.DEFAULT_PAGE;
        size = (size != null && size > 0) ? size : PaginationConstants.DEFAULT_SIZE;
        sort = (sort != null && !sort.isEmpty()) ? sort : List.of(PaginationConstants.DEFAULT_SORT);
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, parseSort());
    }

    private Sort parseSort() {
        List<Sort.Order> orders = new ArrayList<>();

        for (String sortParam : sort) {
            String[] parts = sortParam.split(",");
            String property = parts[0].trim();
            String direction = (parts.length > 1) ? parts[1].trim() : "asc";

            orders.add(new Sort.Order(
                    direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    property
            ));
        }

        return Sort.by(orders);
    }
}
