package daryna.gymfit.services;

import daryna.gymfit.dao.WorkoutRepository;
import daryna.gymfit.dto.*;
import daryna.gymfit.entities.*;
import daryna.gymfit.entities.enums.WorkoutType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@AllArgsConstructor
@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ClientService clientService;

    public Workout findById(long id) {
        return workoutRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Workout workout) {
        workoutRepository.save(workout);
    }

    public List<WorkoutForMySchedulePage> findAllForClient(String email) {
        Client client = clientService.findByEmail(email);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthLater = now.plusWeeks(4);

        List<Object[]> raw = workoutRepository.findUpcomingWorkoutsForClient(client.getId(), oneMonthLater);

        return raw.stream()
                .map(row -> new WorkoutForMySchedulePage(
                        ((Number) row[0]).longValue(),
                        ((LocalDateTime) row[1]),
                        (WorkoutType) row[2],
                        new CoachForBookingWorkoutDto(
                                ((Number) row[3]).longValue(),
                                (String) row[4],
                                (String) row[5]),
                        new FieldDto(
                                ((Number) row[6]).longValue(),
                                (String) row[7]
                        )
                ))
                .toList();
    }


    public List<WorkoutForBookingDto> findUpcomingWorkoutsForCoach(long coachId, LocalDate date) {
        LocalDateTime startOfDay;
        if (date.isEqual(LocalDate.now())) {
            LocalDateTime now = LocalDateTime.now();
            startOfDay = now.withMinute(0).withSecond(0).withNano(0);
            if (now.getMinute() > 0 || now.getSecond() > 0 || now.getNano() > 0) {
                startOfDay = startOfDay.plusHours(1);
            }
        } else {
            startOfDay = date.atStartOfDay();
        }

        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return workoutRepository.findWorkoutsForCoachOnDate(coachId, startOfDay, endOfDay);
    }

    public void createWorkoutIfNotExists(Coach coach, LocalDateTime dateTime, WorkoutType type) {
        boolean exists = workoutRepository.existsByCoachIdAndWorkoutDate(coach.getId(), dateTime);
        if (!exists) {
            Workout workout = new Workout();
            workout.setCoach(coach);
            workout.setWorkoutDate(dateTime);
            workout.setType(type);
            workout.setBooked(false);
            workoutRepository.save(workout);
        }
    }

    public void createGroupWorkoutIfNotExists(Coach coach, GroupClass groupClass, LocalDateTime dateTime) {
        boolean exists = workoutRepository.existsByCoachIdAndWorkoutDate(coach.getId(), dateTime);
        if (!exists) {
            Workout workout = new Workout();
            workout.setCoach(coach);
            workout.setWorkoutDate(dateTime);
            workout.setType(WorkoutType.GROUP);
            workout.setBooked(false);
            workout.setField(groupClass.getField());
            workoutRepository.save(workout);
        }
    }

    public Workout findByCoachIdAndFieldIdAndWorkoutDateAndType(Long coachId, Long fieldId, LocalDateTime workoutDate, WorkoutType type) {
        return workoutRepository.findByCoachIdAndFieldIdAndWorkoutDateAndType(coachId, fieldId, workoutDate, type).orElse(null);
    }
}
