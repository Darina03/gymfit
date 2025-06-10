package daryna.gymfit.entities;

import daryna.gymfit.entities.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "workout_date", nullable = false)
    private LocalDateTime workoutDate;

    @Column(name = "is_booked", nullable = false)
    private boolean isBooked;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private WorkoutType type;

    @ManyToOne
    @JoinColumn(name = "id_coach", nullable = false)
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "id_field")
    private Field field;

    @OneToMany(mappedBy = "workout")
    private List<WorkoutClients> workoutClients;


}
