package daryna.gymfit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "coach_membership_templates")
public class CoachMembershipTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="workout_amount", nullable = false)
    private int workoutAmount;

    @Column(name="price", nullable = false)
    private BigDecimal price;

    @Column(name="is_active",nullable = false)
    private boolean isActive;

    @Column(name="is_full",nullable = false)
    private boolean isFull;

    @ManyToOne
    @JoinColumn(name = "id_coach", nullable = false)
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "id_field", nullable = false)
    private Field field;
}
