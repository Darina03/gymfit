package daryna.gymfit.services;

import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.Workout;
import daryna.gymfit.entities.WorkoutClients;
import daryna.gymfit.entities.enums.WorkoutType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkoutBookingService {

    private final WorkoutClientsService workoutClientsService;
    private final WorkoutService workoutService;
    private final ClientService clientService;
    private final MembershipService membershipService;


    @Transactional
    public void bookWorkout(Long workoutId, long membershipId, String email) {

        Client client = clientService.findByEmail(email);
        Workout workout = workoutService.findById(workoutId);
        Membership membership = membershipService.findById(membershipId);

        boolean alreadyBooked = workoutClientsService.existsByWorkoutIdAndClientId(workoutId, client.getId());
        if (alreadyBooked) {
            throw new IllegalStateException("Клієнт вже забронював це тренування!");
        }

        if (workout.isBooked()) {
            throw new IllegalStateException("Тренування вже заброньоване!");
        }

        if (membership.getWorkoutAmountLeft() == 0) throw new IllegalStateException("У вашому абонементі вже не залишилося вільних тренувань!");

        membership.setWorkoutAmountLeft(membership.getWorkoutAmountLeft() - 1);
        if (membership.getWorkoutAmountLeft() == 0) {
            membership.setActive(false);
            membership.setEndDate(workout.getWorkoutDate().toLocalDate());
        }
        membershipService.save(membership);

        workout.setField(membership.getCoachMembershipTemplate().getField());
        workout.setBooked(true);
        workoutService.save(workout);


        WorkoutClients workoutClient = new WorkoutClients();
        workoutClient.setWorkout(workout);
        workoutClient.setClient(client);
        workoutClient.setBookedDate(LocalDateTime.now());
        workoutClientsService.save(workoutClient);
    }

    @Transactional
    public void cancelWorkout(Long workoutId, String email) {

        Client client = clientService.findByEmail(email);
        Workout workout = workoutService.findById(workoutId);

        if (!LocalDateTime.now().isBefore(workout.getWorkoutDate().minusHours(4))) {
            throw new IllegalStateException("Відміняти заняття можна не менше аніж за 4 години до початку!");
        }

        Membership membership = membershipService
                .findActiveForClientByCoachIdAndFieldId(client.getId(), workout.getCoach().getId(), workout.getField().getId());

        if (membership.getWorkoutAmountLeft()==0) {
            throw new IllegalStateException("Останнє тренування відміняти не можна!");
        }

        if (workout.getType() == WorkoutType.PERSONAL && workout.isBooked()) {
            workout.setBooked(false);
        }

        membership.setWorkoutAmountLeft(membership.getWorkoutAmountLeft() + 1);
        if (!membership.isActive()) {
            membership.setActive(true);
        }

        workout.setField(null);
        membershipService.save(membership);

        workoutClientsService.deleteByWorkoutIdAndClientId(workoutId, client.getId());

        workoutService.save(workout);
    }
}
