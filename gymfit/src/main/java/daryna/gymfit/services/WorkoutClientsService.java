package daryna.gymfit.services;

import daryna.gymfit.dao.WorkoutClientsRepository;
import daryna.gymfit.entities.*;
import daryna.gymfit.entities.enums.WorkoutType;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@AllArgsConstructor
@Service
public class WorkoutClientsService {

    private final WorkoutClientsRepository workoutClientsRepository;
    private final WorkoutService workoutService;


    public WorkoutClients getById(long id) {
        return workoutClientsRepository.getReferenceById(id);
    }

    @Transactional
    public void save(WorkoutClients workoutClients) {
        workoutClientsRepository.save(workoutClients);
    }


    public boolean existsByWorkoutIdAndClientId(Long workoutId, Long clientId) {
        return workoutClientsRepository.existsByWorkoutIdAndClientId(workoutId, clientId);
    }

    public void deleteByWorkoutIdAndClientId(Long workoutId, Long clientId) {
        WorkoutClients wc = workoutClientsRepository.findByWorkoutIdAndClientId(workoutId, clientId).orElseThrow(() ->
                new EntityNotFoundException("Workout with workout id - " + workoutId + " and client id - " + clientId + " not found!"))
        ;
        workoutClientsRepository.delete(wc);
    }

    @Transactional
    public WorkoutClients create(Client client, Workout workout) {
        WorkoutClients workoutClients = new WorkoutClients();
        workoutClients.setBookedDate(LocalDateTime.now());
        workoutClients.setWorkout(workout);
        workoutClients.setClient(client);
        return workoutClientsRepository.save(workoutClients);
    }

    @Transactional
    public void createGroupWorkoutClients(Membership membership) {
        List<ClassSchedule> schedules = new ArrayList<>(membership.getGroupClass().getClassSchedules());
        int totalWorkouts = membership.getGroupClass().getWorkoutAmount();

        Collections.sort(schedules);

        LocalDateTime now = LocalDateTime.now();
        int enrolled = 0;

        Map<ClassSchedule, LocalDateTime> lastGeneratedMap = new HashMap<>();

        while (enrolled < totalWorkouts) {
            for (ClassSchedule schedule : schedules) {
                if (enrolled >= totalWorkouts) break;

                LocalDateTime from = lastGeneratedMap.getOrDefault(schedule, now);
                LocalDateTime nextDateTime = getNextDateTimeForSchedule(schedule.getScheduledDay(), schedule.getClassTime(), from);

                Workout workout = workoutService.findByCoachIdAndFieldIdAndWorkoutDateAndType(
                        membership.getGroupClass().getCoach().getId(),
                        membership.getGroupClass().getField().getId(),
                        nextDateTime,
                        WorkoutType.GROUP);

                if (workout!=null) {

                    boolean alreadyEnrolled = workoutClientsRepository.existsByWorkoutIdAndClientId(workout.getId(), membership.getClient().getId());
                    if (!alreadyEnrolled) {
                        create(membership.getClient(), workout);
                        enrolled++;
                    }
                }

                lastGeneratedMap.put(schedule, nextDateTime.plusMinutes(1));
            }
        }
    }

    private LocalDateTime getNextDateTimeForSchedule(DayOfWeek targetDay, LocalTime targetTime, LocalDateTime from) {
        LocalDate startDate = from.toLocalDate();
        int daysToAdd = (targetDay.getValue() - from.getDayOfWeek().getValue() + 7) % 7;

        LocalDate candidateDate = startDate.plusDays(daysToAdd);
        LocalDateTime candidateDateTime = LocalDateTime.of(candidateDate, targetTime);

        if (daysToAdd == 0 && candidateDateTime.isBefore(from)) {
            candidateDateTime = candidateDateTime.plusWeeks(1);
        }

        return candidateDateTime;
    }
}