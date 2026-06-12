package com.example.contactmanager.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.SoftDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A free-text note attached to a {@link Person}, mapped to the {@code note}
 * table.
 *
 * <p>Notes are soft-deleted: Hibernate's {@link SoftDelete} support turns
 * entity removal into {@code UPDATE ... SET deleted = TRUE} and transparently
 * filters soft-deleted rows out of every query, so deleted notes never
 * surface in the application.</p>
 */
@Entity
@Table(name = "note")
@SoftDelete(columnName = "deleted")
public class Note {

    /** Database identifier; {@code null} until the record is persisted. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long id;

    /** The person this note belongs to. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "note_text")
    private String noteText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    protected Note() {
        // Required by JPA.
    }

    /**
     * @param person   the person the note belongs to
     * @param noteText the note text
     */
    public Note(Person person, String noteText) {
        this.person = person;
        this.noteText = noteText;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Person getPerson() {
        return person;
    }

    public String getNoteText() {
        return noteText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Note{id=%d, createdAt=%s}".formatted(id, createdAt);
    }
}
