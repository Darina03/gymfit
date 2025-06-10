package daryna.gymfit.services;

import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.GroupClass;
import daryna.gymfit.entities.ClassSchedule;
import daryna.gymfit.entities.enums.WorkoutType;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
@Component
public class WorkoutCreationScheduler {

    private final WorkoutService workoutService;
    private final CoachService coachService;
    private final GroupClassService groupClassService;

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void createWorkoutsForCoaches() {

        LocalDate targetDate = LocalDate.now().plusWeeks(4);
        DayOfWeek day = targetDate.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) return;

        List<Coach> coaches = coachService.getAllActiveCoaches();

        for (Coach coach : coaches) {

            List<GroupClass> coachClasses = groupClassService.findByCoach(coach.getId());

            Map<LocalTime, GroupClass> groupClassScheduleMap = new HashMap<>();
            for (GroupClass groupClass : coachClasses) {
                for (ClassSchedule schedule : groupClass.getClassSchedules()) {
                    if (schedule.getScheduledDay().equals(day)) {
                        groupClassScheduleMap.put(schedule.getClassTime(), groupClass);
                    }
                }
            }

            for (int hour = 8; hour <= 18; hour++) {
                if (hour == 12) continue;
                LocalTime time = LocalTime.of(hour, 0);
                LocalDateTime workoutDateTime = LocalDateTime.of(targetDate, time);

                if (groupClassScheduleMap.containsKey(time)) {
                    GroupClass groupClass = groupClassScheduleMap.get(time);
                    workoutService.createGroupWorkoutIfNotExists(coach, groupClass, workoutDateTime);
                } else {
                    workoutService.createWorkoutIfNotExists(coach, workoutDateTime, WorkoutType.PERSONAL);
                }
            }
        }
    }

}
