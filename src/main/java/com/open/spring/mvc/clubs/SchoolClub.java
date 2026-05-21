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
            new SchoolClub("Link Crew", List.of("Charity/Volunteer"), "clubs/link_crew.png")
        };
    }
}