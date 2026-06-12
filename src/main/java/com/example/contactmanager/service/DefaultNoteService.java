package com.example.contactmanager.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.contactmanager.domain.Note;
import com.example.contactmanager.domain.Person;
import com.example.contactmanager.repository.NoteRepository;
import com.example.contactmanager.repository.PersonRepository;

/**
 * Default {@link NoteService} implementation delegating persistence to the
 * Spring Data JPA repositories. Soft deletion is handled by the {@link Note}
 * entity's {@code @SoftDelete} mapping.
 */
@Service
@Transactional
public class DefaultNoteService implements NoteService {

    private final NoteRepository noteRepository;
    private final PersonRepository personRepository;

    /**
     * @param noteRepository   the note repository
     * @param personRepository the person repository
     */
    public DefaultNoteService(NoteRepository noteRepository, PersonRepository personRepository) {
        this.noteRepository = noteRepository;
        this.personRepository = personRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Note> listNotes(UUID personId) {
        requirePersonExists(personId);
        return noteRepository.findByPersonIdOrderByCreatedAtDescIdDesc(personId);
    }

    @Override
    public Note addNote(UUID personId, String noteText) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        return noteRepository.save(new Note(person, noteText));
    }

    @Override
    public void deleteNote(UUID personId, UUID noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(found -> personId.equals(found.getPerson().getId()))
                .orElseThrow(() -> new NoteNotFoundException(personId, noteId));
        noteRepository.delete(note);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, Long> countNotes(Collection<UUID> personIds) {
        Map<UUID, Long> counts = new HashMap<>();
        if (personIds.isEmpty()) {
            return counts;
        }
        for (UUID personId : personIds) {
            counts.put(personId, 0L);
        }
        for (NoteRepository.NoteCountByPerson row : noteRepository.countByPersonIds(personIds)) {
            counts.put(row.getPersonId(), row.getNoteCount());
        }
        return counts;
    }

    private void requirePersonExists(UUID personId) {
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(personId);
        }
    }
}
