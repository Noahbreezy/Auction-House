package be.ehb.auctionhousebackend.controller;


import be.ehb.auctionhousebackend.model.Person;
import be.ehb.auctionhousebackend.service.PersonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Persons", description = "Operations related to users/persons")
@Validated
@RestController
@RequestMapping("/api/v1/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
     List<Person> findAll(@RequestParam(name = "search", required = false) String searchName) {
        if (searchName != null) {
            return personService.findByName(searchName);
        }
        return personService.findAll();
    }

    @PostMapping
    ResponseEntity<Person> save(@Valid @RequestBody Person person) {
        personService.save(person);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/{id}")
    ResponseEntity<Person> findById(@PathVariable("id") String id) {
        return ResponseEntity.ok(personService.findById(id));
    }
}
