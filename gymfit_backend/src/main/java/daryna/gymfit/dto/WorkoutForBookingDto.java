package daryna.gymfit.dto;

import java.time.LocalDateTime;

public record WorkoutForBookingDto(

        long id,

        LocalDateTime workoutDate,

        boolean isBooked
) {
}
