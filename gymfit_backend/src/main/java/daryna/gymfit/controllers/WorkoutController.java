package daryna.gymfit.controllers;

import daryna.gymfit.dto.CoachForBookingWorkoutDto;
import daryna.gymfit.dto.WorkoutForBookingDto;
import daryna.gymfit.dto.WorkoutForMySchedulePage;
import daryna.gymfit.services.WorkoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.time.*;


@RestController
@RequestMapping("/gymfit/workouts")
@RequiredArgsConstructor
public class WorkoutController {

    private final WorkoutService workoutService;


    @GetMapping("/all-for-client")
    public ResponseEntity<List<WorkoutForMySchedulePage>> getAllForClient(Principal principal){
        List<WorkoutForMySchedulePage> workouts=workoutService.findAllForClient(principal.getName());
        return ResponseEntity.ok(workouts);
    }

    @GetMapping("/all-for-coach/{coachId}")
    public ResponseEntity<List<WorkoutForBookingDto>> getAllForCoach(@PathVariable long coachId, @RequestParam LocalDate date){
        return ResponseEntity.ok(workoutService.findUpcomingWorkoutsForCoach(coachId,date));
    }
}
