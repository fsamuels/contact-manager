package com.aquent.contactmanager.controller;

import com.aquent.contactmanager.model.Person;
import com.aquent.contactmanager.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Web controller exposing the CRUD workflows for {@link Person} records.
 *
 * <p>Each mutating action follows the Post/Redirect/Get pattern: a successful POST issues a
 * redirect back to the listing so that a browser refresh does not re-submit the form.</p>
 */
@Controller
@RequestMapping("/people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Person Listing — the main page. Shows all people in the system.
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("people", personService.getAllPeople());
        return "list";
    }

    /**
     * Renders the empty Create Person form.
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("person", new Person());
        return "form";
    }

    /**
     * Handles submission of the Create Person form. On validation failure the form is
     * redisplayed with field-level messages; on success the person is saved and the user
     * is returned to the listing.
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("person") Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        personService.createPerson(person);
        return "redirect:/people";
    }

    /**
     * Renders the Edit Person form pre-populated with the selected person's data.
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        Person person = personService.getPerson(id);
        if (person == null) {
            redirectAttributes.addFlashAttribute("message", "That person no longer exists.");
            return "redirect:/people";
        }
        model.addAttribute("person", person);
        return "form";
    }

    /**
     * Handles submission of the Edit Person form. Validation mirrors the create workflow.
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id,
                       @Valid @ModelAttribute("person") Person person,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }
        person.setId(id);
        personService.updatePerson(person);
        return "redirect:/people";
    }

    /**
     * Renders the Delete Person confirmation page.
     */
    @GetMapping("/{id}/delete")
    public String deleteConfirm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        Person person = personService.getPerson(id);
        if (person == null) {
            redirectAttributes.addFlashAttribute("message", "That person no longer exists.");
            return "redirect:/people";
        }
        model.addAttribute("person", person);
        return "delete";
    }

    /**
     * Performs the deletion and returns the user to the listing.
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        personService.deletePerson(id);
        return "redirect:/people";
    }
}
