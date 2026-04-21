package com.open.spring.mvc.clubMembership;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "club_memberships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClubMembership {

    public enum Status {
        PENDING,
        ACCEPTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String personName;

    @Column(nullable = false)
    private String clubName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    public ClubMembership(String personName, String clubName) {
        this.personName = personName;
        this.clubName = clubName;
        this.status = Status.PENDING;
    }
}