package daryna.gymfit.dto;

import java.time.LocalDate;
import java.util.List;

public record GymAndCoachMembershipsDtoForBooking(

        List<MembershipForBookingDto> coachMemberships,

        GymMembershipForBookingDto gymMembership

) {
}
