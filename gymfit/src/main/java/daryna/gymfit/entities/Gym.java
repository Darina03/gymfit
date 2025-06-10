package daryna.gymfit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "gyms")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", unique = true, nullable = false)
    private int number;

    @Column(name = "address", nullable = false, length = 300)
    private String address;

}
