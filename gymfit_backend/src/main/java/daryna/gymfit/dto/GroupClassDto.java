package daryna.gymfit.dto;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record GroupClassDto(

        Long id,

        int workoutAmount,

        BigDecimal price,

        int maxParticipants,

        int currentEnrollments,

        String coachName,

        String coachSurname,

        String fieldName,

        List<ClassScheduleDto> schedules

) {
}
