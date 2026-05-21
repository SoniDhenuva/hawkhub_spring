package com.open.spring.mvc.clubs;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolClubRepository extends JpaRepository<SchoolClub, Long> {
    List<SchoolClub> findAllByOrderByNameAsc();

    Optional<SchoolClub> findByNameIgnoreCase(String name);
}