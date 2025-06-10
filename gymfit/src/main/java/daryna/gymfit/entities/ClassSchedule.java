package daryna.gymfit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "class_schedules")
public class ClassSchedule implements Comparable<ClassSchedule> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="scheduled_day",nullable = false)
    private DayOfWeek scheduledDay;

    @Column(name="class_time",nullable = false)
    private LocalTime classTime;

    @ManyToOne
    @JoinColumn(name = "id_class", nullable = false)
    private GroupClass groupClass;

    @Override
    public int compareTo(ClassSchedule o) {
        return this.scheduledDay.compareTo(o.scheduledDay);
    }
}
