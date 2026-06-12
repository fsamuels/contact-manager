package com.example.contactmanager.web;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.contactmanager.domain.Page;
import com.example.contactmanager.domain.Person;
import com.example.contactmanager.service.PersonNotFoundException;
import com.example.contactmanager.service.PersonService;

/**
 * Web controller for the person CRUD workflows: listing, creation, editing,
 * and deletion with confirmation.
 */
@Controller
public class PersonController {

    private static final String LIST_VIEW = "person/list";
    private static final String FORM_VIEW = "person/form";
    private static final String DELETE_VIEW = "person/delete-confirm";
    private static final String REDIRECT_TO_LIST = "redirect:/persons";

    /** Page sizes the listing page offers; requests for any other size fall back to the default. */
    private static final List<Integer> PAGE_SIZE_OPTIONS = List.of(10, 25, 50, 100);
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final PersonService personService;

    /**
     * @param personService the person service
     */
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @InitBinder
    void initBinder(WebDataBinder binder) {
        // Trim form input; all-whitespace values bind as null so @NotBlank fires.
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Redirects the application root to the person listing.
     */
    @GetMapping("/")
    public String home() {
        return REDIRECT_TO_LIST;
    }

    /**
     * Displays the main page: a paginated listing of people.
     *
     * @param page the requested 1-based page number; out-of-range values are
     *             clamped to the valid range
     * @param size the requested page size; values other than the offered
     *             options fall back to the default of {@value #DEFAULT_PAGE_SIZE}
     */
    @GetMapping("/persons")
    public String list(@RequestParam(name = "page", defaultValue = "1") int page,
                       @RequestParam(name = "size", defaultValue = "10") int size,
                       Model model) {
        if (!PAGE_SIZE_OPTIONS.contains(size)) {
            size = DEFAULT_PAGE_SIZE;
        }
        Page<Person> personPage = personService.listPeople(page, size);
        model.addAttribute("personPage", personPage);
        model.addAttribute("people", personPage.getItems());
        model.addAttribute("pageSizeOptions", PAGE_SIZE_OPTIONS);
        return LIST_VIEW;
    }

    /**
     * Displays the form for creating a new person.
     */
    @GetMapping("/persons/new")
    public String createForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("formTitle", "Create Person");
        return FORM_VIEW;
    }

    /**
     * Handles submission of the create form. On validation failure the form is
     * redisplayed with field-level messages; on success the person is saved
     * and the user returns to the listing.
     */
    @PostMapping("/persons/new")
    public String create(@Valid @ModelAttribute("person") Person person,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Create Person");
            return FORM_VIEW;
        }
        personService.createPerson(person);
        redirectAttributes.addFlashAttribute("successMessage",
                "Created person: " + person.getFirstName() + " " + person.getLastName());
        return REDIRECT_TO_LIST;
    }

    /**
     * Displays the form for editing an existing person.
     */
    @GetMapping("/persons/{id}/edit")
    public String editForm(@PathVariable("id") long id, Model model) {
        Person person = personService.findPerson(id).orElseThrow(() -> new PersonNotFoundException(id));
        model.addAttribute("person", person);
        model.addAttribute("formTitle", "Edit Person");
        return FORM_VIEW;
    }

    /**
     * Handles submission of the edit form. On validation failure the form is
     * redisplayed with field-level messages; on success the person is saved
     * and the user returns to the listing.
     */
    @PostMapping("/persons/{id}/edit")
    public String edit(@PathVariable("id") long id,
                       @Valid @ModelAttribute("person") Person person,
                       BindingResult bindingResult,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("formTitle", "Edit Person");
            return FORM_VIEW;
        }
        person.setId(id);
        personService.updatePerson(person);
        redirectAttributes.addFlashAttribute("successMessage",
                "Updated person: " + person.getFirstName() + " " + person.getLastName());
        return REDIRECT_TO_LIST;
    }

    /**
     * Displays the delete confirmation page for a person.
     */
    @GetMapping("/persons/{id}/delete")
    public String deleteConfirm(@PathVariable("id") long id, Model model) {
        Person person = personService.findPerson(id).orElseThrow(() -> new PersonNotFoundException(id));
        model.addAttribute("person", person);
        return DELETE_VIEW;
    }

    /**
     * Deletes the person after confirmation and returns to the listing.
     */
    @PostMapping("/persons/{id}/delete")
    public String delete(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        Person person = personService.findPerson(id).orElseThrow(() -> new PersonNotFoundException(id));
        personService.deletePerson(id);
        redirectAttributes.addFlashAttribute("successMessage",
                "Deleted person: " + person.getFirstName() + " " + person.getLastName());
        return REDIRECT_TO_LIST;
    }

    /**
     * Handles references to person records that no longer exist by returning
     * the user to the listing with an explanatory message.
     */
    @ExceptionHandler(PersonNotFoundException.class)
    public String handleNotFound(PersonNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return REDIRECT_TO_LIST;
    }
}
