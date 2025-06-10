package daryna.gymfit.dto;

import daryna.gymfit.entities.enums.WorkoutType;

import java.time.LocalDate;

public record MembershipDto(

        String fieldName,

        String coachName,

        String coachSurname,

        int initialWorkoutAmount,

        int leftWorkoutAmount,

        LocalDate endDate,

        WorkoutType type

) {
}
