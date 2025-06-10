package daryna.gymfit.dao;

import daryna.gymfit.dto.WorkoutForBookingDto;
import daryna.gymfit.dto.WorkoutForMySchedulePage;
import daryna.gymfit.entities.Workout;
import daryna.gymfit.entities.enums.WorkoutType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    @NonNull
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Workout w WHERE w.id = :id")
    Optional<Workout> findById(@Param("id") @NonNull Long id);


    @Query("""
                SELECT
                w.id,
                w.workoutDate,
                w.type,
                c.id,
                c.name,
                c.surname,
                f.id,
                f.name
                FROM Workout w
                JOIN w.workoutClients wc
                JOIN w.coach c
                JOIN w.field f
                WHERE wc.client.id = :clientId
                  AND w.workoutDate BETWEEN CURRENT_TIMESTAMP AND :endDate
                ORDER BY w.workoutDate
            """)
    List<Object[]> findUpcomingWorkoutsForClient(@Param("clientId") Long clientId,
                                                                 @Param("endDate") LocalDateTime endDate);

    @Query("""
                SELECT new daryna.gymfit.dto.WorkoutForBookingDto(
                    w.id,
                    w.workoutDate,
                    w.isBooked
                )
                FROM Workout w
                WHERE w.coach.id = :coachId
                  AND w.workoutDate BETWEEN :startOfDay AND :endOfDay
                ORDER BY w.workoutDate
            """)
    List<WorkoutForBookingDto> findWorkoutsForCoachOnDate(
            @Param("coachId") long coachId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    boolean existsByCoachIdAndWorkoutDate(Long coachId, LocalDateTime dateTime);

    Optional<Workout> findByCoachIdAndFieldIdAndWorkoutDateAndType(
            Long coachId,
            Long fieldId,
            LocalDateTime workoutDate,
            WorkoutType type
    );
}

