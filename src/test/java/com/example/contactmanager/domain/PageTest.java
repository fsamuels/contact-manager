package com.example.contactmanager.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link Page} paging metadata.
 */
class PageTest {

    @Test
    void totalPagesRoundsUp() {
        assertEquals(5, new Page<>(List.of(), 1, 10, 42).getTotalPages());
        assertEquals(4, new Page<>(List.of(), 1, 10, 40).getTotalPages());
        assertEquals(1, new Page<>(List.of(), 1, 10, 1).getTotalPages());
    }

    @Test
    void emptyResultStillHasOnePage() {
        Page<String> page = new Page<>(List.of(), 1, 10, 0);
        assertEquals(1, page.getTotalPages());
        assertTrue(page.isFirst());
        assertTrue(page.isLast());
    }

    @Test
    void firstAndLastReflectPosition() {
        assertTrue(new Page<>(List.of(), 1, 10, 42).isFirst());
        assertFalse(new Page<>(List.of(), 1, 10, 42).isLast());

        Page<String> middle = new Page<>(List.of(), 3, 10, 42);
        assertFalse(middle.isFirst());
        assertFalse(middle.isLast());

        Page<String> last = new Page<>(List.of(), 5, 10, 42);
        assertFalse(last.isFirst());
        assertTrue(last.isLast());
    }

    @Test
    void rejectsNonPositivePageSize() {
        assertThrows(IllegalArgumentException.class, () -> new Page<>(List.of(), 1, 0, 0));
    }
}
