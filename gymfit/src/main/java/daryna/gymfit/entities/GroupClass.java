package daryna.gymfit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "classes")
public class GroupClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="workout_amount", nullable = false)
    private int workoutAmount;

    @Column(name="price", nullable = false)
    private BigDecimal price;

    @Column(name="is_active", nullable = false)
    private boolean isActive;

    @Column(name="is_full", nullable = false)
    private boolean isFull;

    @Column(name="max_participants", nullable = false)
    private int maxParticipants;

    @Column(name="is_gym_membership_required", nullable = false)
    private boolean isGymMembershipRequired;

    @ManyToOne
    @JoinColumn(name = "id_coach", nullable = false)
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "id_field", nullable = false)
    private Field field;

    @ManyToMany
    @JoinTable(
            name = "class_enrollments",
            joinColumns = @JoinColumn(name = "id_class"),
            inverseJoinColumns = @JoinColumn(name = "id_client")
    )
    private List<Client> clients;

    @OneToMany(mappedBy = "groupClass", fetch = FetchType.LAZY)
    private List<ClassSchedule> classSchedules;
}
