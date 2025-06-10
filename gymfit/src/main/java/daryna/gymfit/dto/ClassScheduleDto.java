package daryna.gymfit.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record ClassScheduleDto(
        DayOfWeek scheduledDay,
        LocalTime classTime
) {
}