package daryna.gymfit.dto;

import java.time.LocalDate;

public record CoachEnrollmentMembershipDto(

        LocalDate endDate,

        CoachForClientProfileDto coach
) {
}
