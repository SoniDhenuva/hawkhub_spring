package com.open.spring.mvc.clubMembership;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubMembershipRepository extends JpaRepository<ClubMembership, Long> {
    List<ClubMembership> findByPersonNameIgnoreCaseOrderByIdDesc(String personName);

    List<ClubMembership> findByClubNameIgnoreCaseOrderByIdDesc(String clubName);

    Optional<ClubMembership> findByPersonNameIgnoreCaseAndClubNameIgnoreCase(String personName, String clubName);

    List<ClubMembership> findByStatusOrderByIdDesc(ClubMembership.Status status);
}