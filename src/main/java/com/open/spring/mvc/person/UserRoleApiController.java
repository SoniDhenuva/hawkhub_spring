package com.open.spring.mvc.person;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.Getter;
import lombok.Setter;


@RestController
@RequestMapping("/api/users")
public class UserRoleApiController {


   private static final List<String> MANAGED_ROLES = List.of("ROLE_STUDENT", "ROLE_LEADERSHIP", "ROLE_ADMIN");


   @Autowired
   private PersonJpaRepository personRepository;


   @Autowired
   private PersonRoleJpaRepository roleRepository;


   @Getter
   @Setter
   public static class RoleDto {
       private String role;
   }


   @GetMapping
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
       List<Person> people = personRepository.findAllByOrderByNameAsc();
       List<Map<String, Object>> result = new ArrayList<>();
       for (Person person : people) {
           List<String> roleNames = new ArrayList<>();
           for (PersonRole role : person.getRoles()) {
               roleNames.add(role.getName());
           }
           result.add(Map.of(
               "id", person.getId(),
               "name", person.getName(),
               "email", person.getEmail(),
               "uid", person.getUid(),
               "roles", roleNames
           ));
       }
       return new ResponseEntity<>(result, HttpStatus.OK);
   }


   @PutMapping("/{id}/role")
   @PreAuthorize("hasAuthority('ROLE_ADMIN')")
   public ResponseEntity<Object> updateUserRole(@PathVariable long id, @RequestBody RoleDto body) {
       String requestedRole = body.getRole();
       if (requestedRole == null || requestedRole.isBlank()) {
           return new ResponseEntity<>(Map.of("error", "role field is required"), HttpStatus.BAD_REQUEST);
       }


       String springRoleName = switch (requestedRole.toUpperCase()) {
           case "STUDENT"    -> "ROLE_STUDENT";
           case "LEADERSHIP" -> "ROLE_LEADERSHIP";
           case "ADMIN"      -> "ROLE_ADMIN";
           default -> null;
       };


       if (springRoleName == null) {
           return new ResponseEntity<>(
               Map.of("error", "Invalid role. Must be STUDENT, LEADERSHIP, or ADMIN"),
               HttpStatus.BAD_REQUEST
           );
       }


       Optional<Person> optional = personRepository.findById(id);
       if (optional.isEmpty()) {
           return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
       }
       Person person = optional.get();


       PersonRole newRole = roleRepository.findByName(springRoleName);
       if (newRole == null) {
           newRole = roleRepository.save(new PersonRole(springRoleName));
       }


       // Replace only the three managed roles; keep everything else (e.g. ROLE_USER, ROLE_TEACHER)
       List<PersonRole> updatedRoles = new ArrayList<>();
       for (PersonRole r : person.getRoles()) {
           if (!MANAGED_ROLES.contains(r.getName())) {
               updatedRoles.add(r);
           }
       }
       updatedRoles.add(newRole);
       person.setRoles(updatedRoles);
       personRepository.save(person);


       return new ResponseEntity<>(Map.of(
           "message", "Role updated successfully",
           "id", person.getId(),
           "uid", person.getUid(),
           "role", springRoleName
       ), HttpStatus.OK);
   }
}



