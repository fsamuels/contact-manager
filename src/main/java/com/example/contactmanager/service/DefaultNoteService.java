package com.example.contactmanager.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Note> listNotes(long personId) {
        requirePersonExists(personId);
        return noteRepository.findByPersonIdOrderByCreatedAtDescIdDesc(personId);
    }

    @Override
    public long addNote(long personId, String noteText) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        return noteRepository.save(new Note(person, noteText)).getId();
    }

    @Override
    public void deleteNote(long personId, long noteId) {
        Note note = noteRepository.findById(noteId)
                .filter(found -> found.getPerson().getId() == personId)
                .orElseThrow(() -> new NoteNotFoundException(personId, noteId));
        noteRepository.delete(note);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countNotes(Collection<Long> personIds) {
        Map<Long, Long> counts = new HashMap<>();
        if (personIds.isEmpty()) {
            return counts;
        }
        for (Long personId : personIds) {
            counts.put(personId, 0L);
        }
        for (NoteRepository.NoteCountByPerson row : noteRepository.countByPersonIds(personIds)) {
            counts.put(row.getPersonId(), row.getNoteCount());
        }
        return counts;
    }

    private void requirePersonExists(long personId) {
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(personId);
        }
    }
}
