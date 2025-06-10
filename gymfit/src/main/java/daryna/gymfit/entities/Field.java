package daryna.gymfit.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "fields")
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;


    @ManyToMany(mappedBy = "fields", fetch = FetchType.LAZY)
    private List<Coach> coaches;

}