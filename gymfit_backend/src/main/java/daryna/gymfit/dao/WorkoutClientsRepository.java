package daryna.gymfit.dao;

import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.Workout;
import daryna.gymfit.entities.WorkoutClients;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

@Repository
public interface WorkoutClientsRepository extends JpaRepository<WorkoutClients, Long> {

    public List<WorkoutClients> findByWorkoutId(long workoutId);

    @Query("SELECT wc FROM WorkoutClients wc " +
           "JOIN FETCH wc.workout w " +
           "WHERE wc.client.id = :clientId AND w.workoutDate > CURRENT_TIMESTAMP")
    public List<WorkoutClients> findByClientId(@Param("clientId") long clientId);


    Optional<WorkoutClients> findByWorkoutIdAndClientId(Long workoutId, Long clientId);

    boolean existsByWorkoutIdAndClientId(Long workoutId, Long clientId);
}
