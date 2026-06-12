package com.example.contactmanager.web;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.contactmanager.domain.Person;
import com.example.contactmanager.service.NoteNotFoundException;
import com.example.contactmanager.service.NoteService;
import com.example.contactmanager.service.PersonNotFoundException;
import com.example.contactmanager.service.PersonService;

/**
 * Web controller for managing a person's notes: listing, adding, and soft
 * deletion. Notes cannot be edited.
 */
@Controller
@RequestMapping("/persons/{personId}/notes")
public class NoteController {

    private static final String NOTES_VIEW = "person/notes";
    private static final String REDIRECT_TO_PERSONS = "redirect:/persons";

    private final NoteService noteService;
    private final PersonService personService;

    /**
     * @param noteService   the note service
     * @param personService the person service
     */
    public NoteController(NoteService noteService, PersonService personService) {
        this.noteService = noteService;
        this.personService = personService;
    }

    @InitBinder
    void initBinder(WebDataBinder binder) {
        // Trim form input; all-whitespace values bind as null so @NotBlank fires.
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Displays a person's notes with the add-note form.
     */
    @GetMapping
    public String notes(@PathVariable("personId") UUID personId, Model model) {
        populateNotesModel(personId, model);
        model.addAttribute("noteForm", new NoteForm());
        return NOTES_VIEW;
    }

    /**
     * Handles submission of the add-note form. On validation failure the page
     * is redisplayed with field-level messages; on success the note is saved
     * and the user returns to the person's notes.
     */
    @PostMapping
    public String add(@PathVariable("personId") UUID personId,
                      @Valid @ModelAttribute("noteForm") NoteForm noteForm,
                      BindingResult bindingResult,
                      Model model,
                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateNotesModel(personId, model);
            return NOTES_VIEW;
        }
        noteService.addNote(personId, noteForm.getNoteText());
        redirectAttributes.addFlashAttribute("successMessage", "Added note");
        return redirectToNotes(personId);
    }

    /**
     * Soft-deletes a note and returns to the person's notes.
     */
    @PostMapping("/{noteId}/delete")
    public String delete(@PathVariable("personId") UUID personId,
                         @PathVariable("noteId") UUID noteId,
                         RedirectAttributes redirectAttributes) {
        noteService.deleteNote(personId, noteId);
        redirectAttributes.addFlashAttribute("successMessage", "Deleted note");
        return redirectToNotes(personId);
    }

    /**
     * Handles references to people that no longer exist by returning the user
     * to the listing with an explanatory message.
     */
    @ExceptionHandler(PersonNotFoundException.class)
    public String handlePersonNotFound(PersonNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return REDIRECT_TO_PERSONS;
    }

    /**
     * Handles references to notes that no longer exist (or belong to another
     * person) by returning the user to the person's notes with a message.
     * The person id travels on the exception because {@code @PathVariable}
     * arguments are not resolvable in exception handler methods.
     */
    @ExceptionHandler(NoteNotFoundException.class)
    public String handleNoteNotFound(NoteNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return redirectToNotes(ex.getPersonId());
    }

    private void populateNotesModel(UUID personId, Model model) {
        Person person = personService.findPerson(personId)
                .orElseThrow(() -> new PersonNotFoundException(personId));
        model.addAttribute("person", person);
        model.addAttribute("notes", noteService.listNotes(personId));
    }

    private static String redirectToNotes(UUID personId) {
        return "redirect:/persons/" + personId + "/notes";
    }
}
