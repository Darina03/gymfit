package daryna.gymfit.notification;

import daryna.gymfit.entities.GymMembership;
import daryna.gymfit.entities.Membership;

public interface NotificationService {

    void sendExpirationReminder(Membership membership);

    void sendExpirationReminder(GymMembership membership);
}
