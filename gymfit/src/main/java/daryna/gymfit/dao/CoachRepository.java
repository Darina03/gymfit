package daryna.gymfit.dao;

import daryna.gymfit.dto.CoachForBookingWorkoutDto;
import daryna.gymfit.dto.CoachForClientProfileDto;
import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.enums.CoachAccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {

    public List<Coach> findByNameAndSurname(String name, String surname);

    @Query(value = """
                SELECT c.id, c.name, c.surname, c.pic_url, c.id_gym, STRING_AGG(f.name, ', ') AS fields
                FROM coaches c
                JOIN coach_fields cf ON c.id = cf.id_coach
                JOIN fields f ON f.id = cf.id_field
                WHERE c.status = 'ACTIVE'
                GROUP BY c.id, c.name, c.surname, c.pic_url, c.id_gym
            """, nativeQuery = true)
    List<Object[]> findCoachesWithFields();

    @Query(value = """
                SELECT c.id,
                       c.name,
                       c.surname,
                       c.phone_number,
                       c.birthday,
                       c.email,
                       c.pic_url,
                       c.instagram,
                       c.motto,
                       c.description,
                       c.experience,
                       c.average_rating,
                       c.rank,
                       STRING_AGG(f.name, ', ') AS fields
                FROM coaches c
                JOIN coach_fields cf ON c.id = cf.id_coach
                JOIN fields f ON f.id = cf.id_field
                WHERE c.status = 'ACTIVE' AND c.id = :id
                GROUP BY c.id, c.name, c.surname, c.phone_number, c.birthday, c.email,
                         c.pic_url, c.instagram, c.motto, c.description, c.experience,
                         c.average_rating, c.rank
            """, nativeQuery = true)
    Optional<Object> findCoachInfo(@Param("id") Long id);


    Optional<Coach> findByEmail(String email);


    List<Coach> findByStatus(CoachAccountStatus coachAccountStatus);
}
