package daryna.gymfit.controllers;

import daryna.gymfit.dto.*;
import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.Gym;
import daryna.gymfit.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/gymfit/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;
    private final GymService gymService;
    private final FieldService fieldService;
    private final CoachRatingService coachRatingService;
    private final CommentService commentService;

    @GetMapping("/all")
    public List<CoachForCoachesPageDto> getAllCoaches() {
        return coachService.getCoachesForCoachesPage();
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<FullCoachInfoForProfile> getCoachProfile(@PathVariable Long id, Principal principal) {
        CoachProfileDto coach = coachService.getCoachForProfile(id);
        int rating = principal != null ? coachRatingService.getCurrentUserRating(principal.getName(), id) : 0;
        List<ResponseCommentDto> comments = commentService.getCommentsForCoach(id);
        FullCoachInfoForProfile coachInfo = new FullCoachInfoForProfile(coach, rating, comments);
        return ResponseEntity.ok(coachInfo);
    }

    @GetMapping("/all-for-coaches-page")
    public ResponseEntity<CoachListPageDto> getAllCoachesForCoachesPage() {
        List<CoachForCoachesPageDto> coaches = coachService.getCoachesForCoachesPage();
        List<Gym> gyms = gymService.findAll();
        List<FieldDto> fields = fieldService.findAll();
        return ResponseEntity.ok(new CoachListPageDto(coaches, gyms, fields));
    }

    @PostMapping("/unenroll/{coachId}")
    public ResponseEntity<?> unenrollClient(@PathVariable Long coachId, Principal principal) {
        coachService.unenrollClient(coachId, principal.getName());
        return ResponseEntity.ok("");
    }

}
