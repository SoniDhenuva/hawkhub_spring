package com.open.spring.mvc.clubs;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_clubs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotEmpty
    @Convert(converter = ClubCategoriesConverter.class)
    @Column(nullable = false, columnDefinition = "text")
    private List<String> categories = new ArrayList<>();

    @NotBlank
    @Column(nullable = false)
    private String image;

    public SchoolClub(String name, List<String> categories, String image) {
        this.name = name;
        this.categories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
        this.image = image;
    }

    public static SchoolClub[] init() {
        return new SchoolClub[] {
            new SchoolClub("FRC Team Optix 3749", List.of("STEM", "Competition"), "clubs/optix.png"),
            new SchoolClub("HOSA", List.of("STEM", "Competition"), "clubs/hosa.png"),
            new SchoolClub("Speech & Debate", List.of("Competition"), "clubs/speech_and_debate.png"),
            new SchoolClub("Mock Trial", List.of("Competition"), "clubs/mock_trial.png"),
            new SchoolClub("DECA", List.of("Competition"), "clubs/deca.png"),
            new SchoolClub("Girls In CS", List.of("STEM"), "clubs/girls_in_cs.png"),
            new SchoolClub("South Asian Cultural Show", List.of("Cultural/Society"), "clubs/sacs.png"),
            new SchoolClub("American Cancer Society", List.of("Advocacy/Awareness"), "clubs/acs.png"),
            new SchoolClub("Interact Club", List.of("Charity/Volunteer"), "clubs/interact.png"),
            new SchoolClub("The Featheralist", List.of("Interest/Sport", "Arts"), "clubs/featheralist.png"),
            new SchoolClub("Link Crew", List.of("Charity/Volunteer"), "clubs/link_crew.png"),
            new SchoolClub("Academic Decathlon", List.of("Competition"), "clubs/academic_decathlon.jpg"),
            new SchoolClub("Academic League", List.of("Competition"), "clubs/academic_league.jpg"),
            new SchoolClub("All Girls STEM Society", List.of("STEM", "Advocacy/Awareness"), "clubs/agss.jpg"),
            new SchoolClub("American Pacific Health Foundation", List.of("Advocacy/Awareness"), "clubs/aphf.jpg"),
            new SchoolClub("American Red Cross", List.of("Charity/Volunteer", "Advocacy/Awareness"), "clubs/american_red_cross.jpg"),
            new SchoolClub("Animation Club", List.of("Arts"), "clubs/animation_club.png"),
            new SchoolClub("Art Cares", List.of("Arts"), "clubs/art_cares.jpg"),
            new SchoolClub("Best Buddies", List.of("Charity/Volunteer", "Advocacy/Awareness"), "clubs/best_buddies.png"),
            new SchoolClub("Del Norte Birding Club", List.of("Interest/Sport", "STEM"), "clubs/birding.jpg"),
            new SchoolClub("Bio Club", List.of("STEM", "Competition"), "clubs/bio_club.jpg"),
            new SchoolClub("Black Student Union", List.of("Cultural/Society", "Advocacy/Awareness"), "clubs/bsu.jpg"),
            new SchoolClub("Boys Rugby", List.of("Interest/Sport"), "clubs/boys_rugby.jpg"),
            new SchoolClub("Brave, Not Perfect", List.of("Advocacy/Awareness"), "clubs/brave_not_perfect.png"),
            new SchoolClub("Care Club", List.of("Charity/Volunteer"), "clubs/CARE.jpg"),
            new SchoolClub("Chess Club", List.of("Competition", "Interest/Sport"), "clubs/chess.jpg"),
            new SchoolClub("Child Rights and You", List.of("Advocacy/Awareness", "Charity/Volunteer"), "clubs/cry.jpg"),
            new SchoolClub("Christians in Action", List.of("Cultural/Society"), "clubs/cia.jpg")
        };
    }
}