package daryna.gymfit.services;

import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.GymMembership;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Component
public class MembershipScheduler {

    private final MembershipService membershipService;
    private final GymMembershipService gymMembershipService;


    @Scheduled(cron = "0 0 0 * * ?")
    public void markExpiredCoachMembershipsAsInactive() {
        membershipService.findExpiredAndMark(LocalDate.now().minusDays(1));

    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void markExpiredGymMembershipsAsInactive() {
        gymMembershipService.findExpiredAndMark(LocalDate.now().minusDays(1));

    }
}
