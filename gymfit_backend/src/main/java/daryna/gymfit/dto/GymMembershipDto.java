package daryna.gymfit.dto;

import daryna.gymfit.entities.enums.GymMembershipType;

import java.time.LocalDate;

public record GymMembershipDto(

        GymMembershipType type,

        LocalDate endDate
) {
}
