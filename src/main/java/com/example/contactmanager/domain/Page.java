package com.example.contactmanager.domain;

import java.util.List;

/**
 * An immutable slice of a result list together with the paging metadata
 * needed to render pagination controls.
 *
 * @param <T> the item type
 */
public final class Page<T> {

    private final List<T> items;
    private final int pageNumber;
    private final int pageSize;
    private final long totalItems;

    /**
     * @param items      the items on this page
     * @param pageNumber the 1-based page number
     * @param pageSize   the maximum number of items per page; must be positive
     * @param totalItems the total number of items across all pages
     */
    public Page(List<T> items, int pageNumber, int pageSize, long totalItems) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be positive: " + pageSize);
        }
        this.items = List.copyOf(items);
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    /**
     * @return the items on this page
     */
    public List<T> getItems() {
        return items;
    }

    /**
     * @return the 1-based page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * @return the maximum number of items per page
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return the total number of items across all pages
     */
    public long getTotalItems() {
        return totalItems;
    }

    /**
     * @return the total number of pages; at least 1 even when there are no items
     */
    public int getTotalPages() {
        return (int) Math.max(1, (totalItems + pageSize - 1) / pageSize);
    }

    /**
     * @return whether this is the first page
     */
    public boolean isFirst() {
        return pageNumber <= 1;
    }

    /**
     * @return whether this is the last page
     */
    public boolean isLast() {
        return pageNumber >= getTotalPages();
    }
}
