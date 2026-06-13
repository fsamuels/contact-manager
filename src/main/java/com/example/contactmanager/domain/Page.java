package com.example.contactmanager.domain;

import java.util.List;

/**
 * An immutable slice of a result list together with the paging metadata
 * needed to render pagination controls.
 *
 * @param <T>        the item type
 * @param items      the items on this page
 * @param pageNumber the 1-based page number
 * @param pageSize   the maximum number of items per page; must be positive
 * @param totalItems the total number of items across all pages
 */
public record Page<T>(List<T> items, int pageNumber, int pageSize, long totalItems) {

    public Page {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be positive: " + pageSize);
        }
        items = List.copyOf(items);
    }

    /**
     * @return the total number of pages; at least 1 even when there are no items
     */
    public int totalPages() {
        return (int) Math.max(1, (totalItems + pageSize - 1) / pageSize);
    }

    /**
     * @return whether this is the first page
     */
    public boolean first() {
        return pageNumber <= 1;
    }

    /**
     * @return whether this is the last page
     */
    public boolean last() {
        return pageNumber >= totalPages();
    }
}
