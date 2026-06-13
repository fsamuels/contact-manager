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
        assertEquals(5, new Page<>(List.of(), 1, 10, 42).totalPages());
        assertEquals(4, new Page<>(List.of(), 1, 10, 40).totalPages());
        assertEquals(1, new Page<>(List.of(), 1, 10, 1).totalPages());
    }

    @Test
    void emptyResultStillHasOnePage() {
        Page<String> page = new Page<>(List.of(), 1, 10, 0);
        assertEquals(1, page.totalPages());
        assertTrue(page.first());
        assertTrue(page.last());
    }

    @Test
    void firstAndLastReflectPosition() {
        assertTrue(new Page<>(List.of(), 1, 10, 42).first());
        assertFalse(new Page<>(List.of(), 1, 10, 42).last());

        Page<String> middle = new Page<>(List.of(), 3, 10, 42);
        assertFalse(middle.first());
        assertFalse(middle.last());

        Page<String> last = new Page<>(List.of(), 5, 10, 42);
        assertFalse(last.first());
        assertTrue(last.last());
    }

    @Test
    void rejectsNonPositivePageSize() {
        assertThrows(IllegalArgumentException.class, () -> new Page<>(List.of(), 1, 0, 0));
    }
}
