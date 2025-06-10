package daryna.gymfit.notification;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.GymMembership;
import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.enums.WorkoutType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService implements NotificationService {

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Override
    public void sendExpirationReminder(Membership membership) {
        String type = switch (membership.getType()) {
            case PERSONAL -> "персональні";
            case GROUP -> "групові";
        };
        Coach coach = membership.getCoachMembershipTemplate().getCoach();
        Client client = membership.getClient();
        String messageBody = String.format("Привіт, %s! Нагадуємо, що ваш абонемент на %s тренування з %s під керівництвом %s закінчиться через 3 дні. Поспіши придбати новий 💪!",
                client.getName(),type, membership.getCoachMembershipTemplate().getField().getName(), coach.getName() + " " + coach.getSurname());
        Message message = Message.creator(
                new PhoneNumber(membership.getClient().getPhoneNumber()),
                new PhoneNumber(twilioPhoneNumber),
                messageBody
        ).create();

//        return message.getSid();
    }

    @Override
    public void sendExpirationReminder(GymMembership membership) {
        Client client = membership.getClient();

        String messageBody = String.format("Привіт, %s! Нагадуємо, що ваш абонемент доступу до спортзалу закінчиться через 3 дні." +
                " Поспіши придбати новий 💪!",client.getName());
        Message message = Message.creator(
                new PhoneNumber(membership.getClient().getPhoneNumber()),
                new PhoneNumber(twilioPhoneNumber),
                messageBody
        ).create();

        //        return message.getSid();
    }
}