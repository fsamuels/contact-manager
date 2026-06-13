package com.example.contactmanager.api;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.contactmanager.domain.Page;
import com.example.contactmanager.domain.Person;
import com.example.contactmanager.service.NoteService;
import com.example.contactmanager.service.PersonNotFoundException;
import com.example.contactmanager.service.PersonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST API for person records. Errors are returned as RFC 9457 problem
 * details (see {@link ApiExceptionHandler}).
 */
@RestController
@RequestMapping("/api/persons")
@Tag(name = "Persons", description = "CRUD operations for person records")
public class PersonApiController {

    private final PersonService personService;
    private final NoteService noteService;

    /**
     * @param personService the person service
     * @param noteService   the note service, for note counts
     */
    public PersonApiController(PersonService personService, NoteService noteService) {
        this.personService = personService;
        this.noteService = noteService;
    }

    /**
     * Lists a page of persons ordered by last name then first name.
     */
    @GetMapping
    @Operation(summary = "List persons (paginated)")
    public PageDto<PersonDto> list(@RequestParam(name = "page", defaultValue = "1") int page,
                                   @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) int size) {
        Page<Person> personPage = personService.listPeople(page, size);
        List<UUID> ids = personPage.items().stream().map(Person::getId).toList();
        var noteCounts = noteService.countNotes(ids);
        return PageDto.of(personPage, person -> PersonDto.of(person, noteCounts.get(person.getId())));
    }

    /**
     * Returns a single person by id.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a person")
    public PersonDto get(@PathVariable("id") UUID id) {
        return toDto(personService.findPerson(id).orElseThrow(() -> new PersonNotFoundException(id)));
    }

    /**
     * Creates a person and returns it with a Location header.
     */
    @PostMapping
    @Operation(summary = "Create a person")
    public ResponseEntity<PersonDto> create(@Valid @RequestBody PersonRequest request) {
        UUID id = personService.createPerson(request.toEntity());
        PersonDto dto = get(id);
        return ResponseEntity.created(URI.create("/api/persons/" + id)).body(dto);
    }

    /**
     * Updates an existing person.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a person")
    public PersonDto update(@PathVariable("id") UUID id, @Valid @RequestBody PersonRequest request) {
        Person person = request.toEntity();
        person.setId(id);
        personService.updatePerson(person);
        return get(id);
    }

    /**
     * Deletes a person (and, via the database cascade, their notes).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a person")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    private PersonDto toDto(Person person) {
        long noteCount = noteService.countNotes(List.of(person.getId())).get(person.getId());
        return PersonDto.of(person, noteCount);
    }
}
