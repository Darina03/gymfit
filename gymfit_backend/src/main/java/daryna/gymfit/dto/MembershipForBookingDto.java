package daryna.gymfit.dto;

import java.time.LocalDate;

public record MembershipForBookingDto(

        Long id,

        LocalDate validTill,

        String fieldName,

        CoachForBookingWorkoutDto coach
) {
}
