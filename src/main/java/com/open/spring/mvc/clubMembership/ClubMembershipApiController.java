package com.open.spring.mvc.clubMembership;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@CrossOrigin(origins = {"http://localhost:8585", "https://pages.opencodingsociety.com"})
@RestController
@RequestMapping("/api/club-memberships")
@Validated
public class ClubMembershipApiController {

    @Autowired
    private ClubMembershipRepository repository;

    @Data
    public static class ClubMembershipRequest {
        @NotBlank
        private String personName;

        @NotBlank
        private String clubName;

        private String status;
    }

    @Data
    public static class ClubMembershipStatusRequest {
        @NotBlank
        private String status;
    }

    @PostMapping
    public ResponseEntity<ClubMembership> create(@Valid @RequestBody ClubMembershipRequest request) {
        try {
            ClubMembership membership = new ClubMembership(request.getPersonName(), request.getClubName());
            membership.setStatus(parseStatus(request.getStatus()));
            ClubMembership saved = repository.save(membership);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<ClubMembership>> getAll() {
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubMembership> getById(@PathVariable Long id) {
        Optional<ClubMembership> membership = repository.findById(id);
        return membership.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/person/{personName}")
    public ResponseEntity<List<ClubMembership>> getByPersonName(@PathVariable String personName) {
        return new ResponseEntity<>(repository.findByPersonNameIgnoreCaseOrderByIdDesc(personName), HttpStatus.OK);
    }

    @GetMapping("/club/{clubName}")
    public ResponseEntity<List<ClubMembership>> getByClubName(@PathVariable String clubName) {
        return new ResponseEntity<>(repository.findByClubNameIgnoreCaseOrderByIdDesc(clubName), HttpStatus.OK);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ClubMembership>> getByStatus(@PathVariable String status) {
        try {
            ClubMembership.Status parsedStatus = parseStatus(status);
            return new ResponseEntity<>(repository.findByStatusOrderByIdDesc(parsedStatus), HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClubMembership> updateStatus(@PathVariable Long id,
            @Valid @RequestBody ClubMembershipStatusRequest request) {
        Optional<ClubMembership> membership = repository.findById(id);
        if (membership.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            ClubMembership clubMembership = membership.get();
            clubMembership.setStatus(parseStatus(request.getStatus()));
            return new ResponseEntity<>(repository.save(clubMembership), HttpStatus.OK);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private ClubMembership.Status parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return ClubMembership.Status.PENDING;
        }

        try {
            return ClubMembership.Status.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("status must be PENDING or ACCEPTED");
        }
    }
}