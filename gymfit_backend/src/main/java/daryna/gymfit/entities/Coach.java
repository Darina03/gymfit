package daryna.gymfit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import daryna.gymfit.entities.enums.CoachAccountStatus;
import daryna.gymfit.entities.enums.CoachRank;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "coaches")
public class Coach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "pic_url")
    private String picUrl;

    @Column(name = "instagram", unique = true, length = 100)
    private String instagram;

    @Column(name = "motto", nullable = false, length = 300)
    private String motto;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "experience", nullable = false)
    private int experience;

    @Column(name = "average_rating")
    private double averageRating;

    @Column(name="personal_clients_limit",nullable = false)
    private int personalClientsLimit;

    @Enumerated(EnumType.STRING)
    @Column(name = "rank", nullable = false, length = 50)
    private CoachRank rank;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private CoachAccountStatus status;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @ManyToOne
    @JoinColumn(name = "id_gym")
    private Gym gym;

    @ManyToMany
    @JoinTable(
            name = "coach_fields",
            joinColumns = @JoinColumn(name = "id_coach"),
            inverseJoinColumns = @JoinColumn(name = "id_field")
    )
    private List<Field> fields;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "coach_clients",
            joinColumns = @JoinColumn(name = "id_coach"),
            inverseJoinColumns = @JoinColumn(name = "id_client")
    )
    private List<Client> clients;


}
