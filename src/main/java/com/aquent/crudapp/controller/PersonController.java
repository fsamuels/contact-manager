package com.aquent.crudapp.controller;

import com.aquent.crudapp.model.Person;
import com.aquent.crudapp.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Handles all CRUD operations for {@link Person} records.
 */
@Controller
@RequestMapping("/person")
public class PersonController {

    private static final String REDIRECT_LIST = "redirect:/person/list";

    @Autowired
    private PersonService personService;

    /**
     * Displays the person listing page.
     */
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("people", personService.listPeople());
        return "person/list";
    }

    /**
     * Displays the create person form.
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("errors", null);
        return "person/form";
    }

    /**
     * Processes the create person form submission.
     */
    @PostMapping("/create")
    public String createSubmit(@ModelAttribute Person person, Model model) {
        List<String> errors = personService.createPerson(person);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("person", person);
            return "person/form";
        }
        return REDIRECT_LIST;
    }

    /**
     * Displays the edit person form pre-populated with existing data.
     */
    @GetMapping("/edit/{personId}")
    public String editForm(@PathVariable Integer personId, Model model) {
        model.addAttribute("person", personService.readPerson(personId));
        model.addAttribute("errors", null);
        return "person/form";
    }

    /**
     * Processes the edit person form submission.
     */
    @PostMapping("/edit/{personId}")
    public String editSubmit(@PathVariable Integer personId,
                             @ModelAttribute Person person,
                             Model model) {
        person.setPersonId(personId);
        List<String> errors = personService.updatePerson(person);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("person", person);
            return "person/form";
        }
        return REDIRECT_LIST;
    }

    /**
     * Displays the delete confirmation page.
     */
    @GetMapping("/delete/{personId}")
    public String deleteConfirm(@PathVariable Integer personId, Model model) {
        model.addAttribute("person", personService.readPerson(personId));
        return "person/delete";
    }

    /**
     * Performs the deletion and redirects to the listing.
     */
    @PostMapping("/delete/{personId}")
    public String deleteSubmit(@PathVariable Integer personId,
                               @RequestParam String action) {
        if ("delete".equals(action)) {
            personService.deletePerson(personId);
        }
        return REDIRECT_LIST;
    }
}
