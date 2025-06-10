package daryna.gymfit.dto;

import java.time.LocalDate;

public record GroupClassEnrollmentMembershipDto(

        LocalDate endDate,

        GroupClassEnrollmentDto groupClass
) {
}
