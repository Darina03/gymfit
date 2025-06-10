package daryna.gymfit.notification;

import daryna.gymfit.services.GymMembershipService;
import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.GymMembership;
import daryna.gymfit.services.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MembershipExpirationNotifier {

    private final GymMembershipService gymMembershipService;
    private final MembershipService membershipService;
    private final NotificationService notificationService;

    @Transactional
    @Scheduled(cron = "0 34 23 * * *")
    public void notifyCoachMembershipClients() {
        List<Membership> expiringMemberships = membershipService.findExpiringOn(LocalDate.now().plusDays(3));
        expiringMemberships.forEach(notificationService::sendExpirationReminder);

    }

    @Scheduled(cron = "0 0 7 * * *")
    public void notifyGymMembershipClients() {
        List<GymMembership> expiringMemberships = gymMembershipService.findExpiringOn(LocalDate.now().plusDays(3));

        expiringMemberships.forEach(notificationService::sendExpirationReminder);

    }


}
