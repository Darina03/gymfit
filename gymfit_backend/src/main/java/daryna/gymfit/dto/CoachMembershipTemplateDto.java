package daryna.gymfit.dto;

import java.math.BigDecimal;

public record CoachMembershipTemplateDto(

        Long id,

        int workoutAmount,

        BigDecimal price,

        String coachName,

        String coachSurname,

        String fieldName

) {
}