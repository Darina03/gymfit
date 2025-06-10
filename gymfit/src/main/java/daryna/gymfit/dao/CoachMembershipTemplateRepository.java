package daryna.gymfit.dao;

import daryna.gymfit.dto.CoachMembershipTemplateDto;
import daryna.gymfit.entities.CoachMembershipTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachMembershipTemplateRepository extends JpaRepository<CoachMembershipTemplate,Long> {

    @Query("SELECT new daryna.gymfit.dto.CoachMembershipTemplateDto(" +
            "ct.id, ct.workoutAmount, ct.price, " +
            "ct.coach.name, ct.coach.surname, ct.field.name) " +
            "FROM CoachMembershipTemplate ct " +
            "WHERE ct.isActive = true AND ct.isFull = false")
    List<CoachMembershipTemplateDto> findAvailableOnly();

    @Query("""
    SELECT new daryna.gymfit.dto.CoachMembershipTemplateDto(
        ct.id, ct.workoutAmount, ct.price,
        ct.coach.name, ct.coach.surname, ct.field.name)
    FROM CoachMembershipTemplate ct
    WHERE ct.isActive = true AND (
          ct.isFull = false
          OR EXISTS (
              SELECT 1 FROM Coach c JOIN c.clients cl
              WHERE c.id = ct.coach.id AND cl.id = :clientId
          )
    )
""")
    List<CoachMembershipTemplateDto> findAvailableOrBelongsToClient(@Param("clientId") Long clientId);

    List<CoachMembershipTemplate> findAllByCoachId(Long coachId);
}
