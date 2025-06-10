package daryna.gymfit.dao;

import daryna.gymfit.entities.CoachRating;
import daryna.gymfit.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByCoachIdOrderByCreationDateDesc(Long coachId);
}
