package daryna.gymfit.dao;

import daryna.gymfit.entities.CoachRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRatingRepository extends JpaRepository<CoachRating, Long> {

    public List<CoachRating> findByCoachId(Long coachId);

    Optional<CoachRating> findByClientIdAndCoachId(Long clientId, Long coachId);
}
