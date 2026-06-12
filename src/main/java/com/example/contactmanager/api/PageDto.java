package com.example.contactmanager.api;

import java.util.List;
import java.util.function.Function;

import com.example.contactmanager.domain.Page;

/**
 * REST page envelope wrapping a list of items with paging metadata.
 *
 * @param <T> the item type
 */
public record PageDto<T>(
        List<T> items,
        int pageNumber,
        int pageSize,
        long totalItems,
        int totalPages) {

    /**
     * @param page   the domain page
     * @param mapper maps each domain item to its REST representation
     * @param <S>    the domain item type
     * @param <T>    the REST item type
     * @return the REST page envelope
     */
    public static <S, T> PageDto<T> of(Page<S> page, Function<S, T> mapper) {
        return new PageDto<>(
                page.getItems().stream().map(mapper).toList(),
                page.getPageNumber(),
                page.getPageSize(),
                page.getTotalItems(),
                page.getTotalPages());
    }
}
