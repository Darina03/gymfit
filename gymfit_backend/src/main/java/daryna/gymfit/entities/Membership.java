package daryna.gymfit.entities;

import daryna.gymfit.entities.enums.WorkoutType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "memberships")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private WorkoutType type;

    @Column(name = "workout_amount_left", nullable = false)
    private int workoutAmountLeft;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "id_client", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "id_class")
    private GroupClass groupClass;

    @ManyToOne
    @JoinColumn(name = "id_coach_membership_template")
    private CoachMembershipTemplate coachMembershipTemplate;

}
