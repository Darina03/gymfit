package daryna.gymfit.dto;
import daryna.gymfit.entities.enums.WorkoutType;
import java.time.LocalDateTime;

public record WorkoutForMySchedulePage(

        long id,

        LocalDateTime workoutDate,

        WorkoutType type,

        CoachForBookingWorkoutDto coach,

        FieldDto field
) {
}
