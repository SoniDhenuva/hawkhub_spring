package com.open.spring.mvc.clubs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@CrossOrigin(origins = {"http://localhost:8585", "http://localhost:4500", "https://pages.opencodingsociety.com"})
@RestController
@RequestMapping("/api/clubs")
@Validated
public class SchoolClubController {

    @Autowired
    private SchoolClubRepository repository;

    @Data
    public static class SchoolClubRequest {
        @NotBlank
        private String name;

        @NotEmpty
        private List<String> categories;

        @NotBlank
        private String image;
    }

    @GetMapping
    public ResponseEntity<List<SchoolClub>> getAll(@RequestParam(required = false) String category) {
        List<SchoolClub> clubs = repository.findAllByOrderByNameAsc();
        if (category == null || category.trim().isEmpty()) {
            return new ResponseEntity<>(clubs, HttpStatus.OK);
        }

        String targetCategory = category.trim();
        List<SchoolClub> filtered = new ArrayList<>();
        for (SchoolClub club : clubs) {
            if (club.getCategories().stream().anyMatch(value -> value.equalsIgnoreCase(targetCategory))) {
                filtered.add(club);
            }
        }

        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SchoolClub> getById(@PathVariable Long id) {
        Optional<SchoolClub> club = repository.findById(id);
        return club.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<SchoolClub> getByName(@PathVariable String name) {
        Optional<SchoolClub> club = repository.findByNameIgnoreCase(name);
        return club.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<SchoolClub> create(@Valid @RequestBody SchoolClubRequest request) {
        if (repository.findByNameIgnoreCase(request.getName()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        SchoolClub saved = repository.save(new SchoolClub(request.getName(), request.getCategories(), request.getImage()));
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SchoolClub> update(@PathVariable Long id, @Valid @RequestBody SchoolClubRequest request) {
        Optional<SchoolClub> club = repository.findById(id);
        if (club.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<SchoolClub> existingClub = repository.findByNameIgnoreCase(request.getName());
        if (existingClub.isPresent() && !existingClub.get().getId().equals(id)) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        SchoolClub schoolClub = club.get();
        schoolClub.setName(request.getName());
        schoolClub.setCategories(new ArrayList<>(request.getCategories()));
        schoolClub.setImage(request.getImage());
        return new ResponseEntity<>(repository.save(schoolClub), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}