package daryna.gymfit.controllers;

import daryna.gymfit.services.WorkoutBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gymfit/booking")
public class WorkoutBookingController {

    private final WorkoutBookingService workoutBookingService;


    @PostMapping("/book/{workoutId}")
    public ResponseEntity<?> bookWorkout(@PathVariable long workoutId, @RequestParam long membershipId, Principal principal){
        workoutBookingService.bookWorkout(workoutId, membershipId, principal.getName());
        return ResponseEntity.ok("");
    }

    @PostMapping("/cancel/{workoutId}")
    public ResponseEntity<?> cancelWorkout(@PathVariable long workoutId, Principal principal){
        workoutBookingService.cancelWorkout(workoutId, principal.getName());
        return ResponseEntity.ok("");
    }
}
