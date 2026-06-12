package com.example.contactmanager.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.contactmanager.service.NoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST API for a person's notes. Notes can only be listed, added, and
 * deleted (deletion is a soft delete). Errors are returned as RFC 9457
 * problem details (see {@link ApiExceptionHandler}).
 */
@RestController
@RequestMapping("/api/persons/{personId}/notes")
@Tag(name = "Notes", description = "Notes attached to a person (add and soft-delete only)")
public class NoteApiController {

    private final NoteService noteService;

    /**
     * @param noteService the note service
     */
    public NoteApiController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Lists a person's active notes, newest first.
     */
    @GetMapping
    @Operation(summary = "List a person's notes")
    public List<NoteDto> list(@PathVariable("personId") UUID personId) {
        return noteService.listNotes(personId).stream().map(NoteDto::of).toList();
    }

    /**
     * Adds a note to a person and returns it with a Location header.
     */
    @PostMapping
    @Operation(summary = "Add a note")
    public ResponseEntity<NoteDto> add(@PathVariable("personId") UUID personId,
                                       @Valid @RequestBody NoteRequest request) {
        NoteDto dto = NoteDto.of(noteService.addNote(personId, request.noteText()));
        return ResponseEntity
                .created(URI.create("/api/persons/" + personId + "/notes/" + dto.id()))
                .body(dto);
    }

    /**
     * Soft-deletes a person's note: the row is retained with its deleted flag
     * set and no longer appears anywhere in the application.
     */
    @DeleteMapping("/{noteId}")
    @Operation(summary = "Delete a note (soft delete)")
    public ResponseEntity<Void> delete(@PathVariable("personId") UUID personId,
                                       @PathVariable("noteId") UUID noteId) {
        noteService.deleteNote(personId, noteId);
        return ResponseEntity.noContent().build();
    }
}
