package daryna.gymfit.dto;

import java.util.List;

public record ClientDashboardDto(

        ClientProfileDto client,

        GymMembershipDto gymMembership,

        List<MembershipDto> personalMemberships,

        List<MembershipDto> groupMemberships,

        List<CoachEnrollmentMembershipDto> coaches,

        List<GroupClassEnrollmentMembershipDto> enrolledGroupClasses
) {
}
