package daryna.gymfit.controllers;

import daryna.gymfit.authentification.AuthService;
import daryna.gymfit.authentification.JwtService;
import daryna.gymfit.dto.RatingRequestDto;
import daryna.gymfit.entities.Client;
import daryna.gymfit.services.CoachRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/gymfit/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final CoachRatingService coachRatingService;


    @PostMapping("/{coachId}")
    public ResponseEntity<?> submitRating(
            @PathVariable Long coachId,
            @RequestBody RatingRequestDto rating, Principal principal){
        coachRatingService.submitRating(principal.getName(), coachId, rating.rating());
        return ResponseEntity.ok("Rating submitted");
    }
}