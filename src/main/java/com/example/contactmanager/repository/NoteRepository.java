package com.example.contactmanager.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.contactmanager.domain.Note;

/**
 * Spring Data JPA repository for {@link Note} records. Soft-deleted notes are
 * filtered out of all queries by the entity's {@code @SoftDelete} mapping.
 */
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Lists a person's notes, newest first.
     *
     * @param personId the person id
     * @return the person's active notes
     */
    List<Note> findByPersonIdOrderByCreatedAtDescIdDesc(long personId);

    /**
     * Counts active notes per person for the given person ids. People with no
     * active notes have no entry in the result.
     *
     * @param personIds the person ids to count notes for
     * @return one row per person that has at least one active note
     */
    @Query("""
            select n.person.id as personId, count(n) as noteCount
            from Note n
            where n.person.id in :personIds
            group by n.person.id
            """)
    List<NoteCountByPerson> countByPersonIds(@Param("personIds") Collection<Long> personIds);

    /**
     * Projection for {@link #countByPersonIds(Collection)}.
     */
    interface NoteCountByPerson {

        Long getPersonId();

        long getNoteCount();
    }
}
