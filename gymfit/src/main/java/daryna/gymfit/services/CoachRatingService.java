package daryna.gymfit.services;

import daryna.gymfit.authentification.JwtService;
import daryna.gymfit.dao.CoachRatingRepository;
import daryna.gymfit.dao.CoachRepository;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.CoachRating;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CoachRatingService {

    private final CoachRatingRepository coachRatingRepository;
    private final ClientService clientService;
    private final CoachService coachService;

    public List<CoachRating> findAllForCoach(Long coachId) {
        return coachRatingRepository.findByCoachId(coachId);
    }


    public Integer getCurrentUserRating(String email, Long coachId) {
       Client client = clientService.findByEmail(email);
        return coachRatingRepository.findByClientIdAndCoachId(client.getId(), coachId)
                .map(CoachRating::getRating)
                .orElse(0);
    }

    public void submitRating(String email, Long coachId, int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        Client client=clientService.findByEmail(email);
        CoachRating rating = coachRatingRepository.findByClientIdAndCoachId(client.getId(), coachId)
                .orElse(new CoachRating());

        rating.setClient(clientService.getClientById(client.getId()));
        rating.setCoach(coachService.getCoachById(coachId));
        rating.setRating(value);

        coachRatingRepository.save(rating);
        calculate_coachAvgRating(coachId);
    }

    public void calculate_coachAvgRating(Long coachId){
        List<CoachRating> ratings=findAllForCoach(coachId);

        double average = ratings.stream()
                .mapToInt(CoachRating::getRating)
                .average()
                .orElse(0.0);

        Coach coach = coachService.getCoachById(coachId);
        coach.setAverageRating(average);
        coachService.save(coach);
    }

}
