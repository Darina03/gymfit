package daryna.gymfit.dto;

import daryna.gymfit.entities.enums.GymMembershipType;

import java.time.LocalDate;

public record GymMembershipForBookingDto(

        LocalDate validTill,

        GymMembershipType type
) {
}
