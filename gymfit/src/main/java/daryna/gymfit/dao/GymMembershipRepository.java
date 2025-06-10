package daryna.gymfit.dao;

import daryna.gymfit.dto.GymMembershipDto;
import daryna.gymfit.dto.GymMembershipForBookingDto;
import daryna.gymfit.entities.GymMembership;
import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.enums.GymMembershipType;
import daryna.gymfit.entities.enums.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GymMembershipRepository extends JpaRepository<GymMembership, Long> {

    Optional<GymMembership> findByActiveTrueAndClientId(long clientId);

    Optional<GymMembership> findTopByClientIdOrderByEndDateDesc(long clientId);

    boolean existsByClientEmailAndActiveTrue(String email);

    @Query("""
        SELECT new daryna.gymfit.dto.GymMembershipDto(
            gm.type,
            gm.endDate
        )
        FROM GymMembership gm
        WHERE gm.client.id = :clientId
          AND gm.active = true
        ORDER BY gm.endDate DESC
        LIMIT 1
    """)
    Optional<GymMembershipDto> getActiveGymMembershipDtoByClientId(@Param("clientId") long clientId);

    @Query("""
           SELECT new daryna.gymfit.dto.GymMembershipForBookingDto(
           m.endDate,
           m.type
           )
           FROM GymMembership m
           WHERE m.client.id=:clientId
           AND m.active = TRUE
""")
    Optional<GymMembershipForBookingDto> findActiveByClientId(@Param("clientId") Long clientId);

    List<GymMembership> findByEndDateAndActive(LocalDate endDate, boolean isActive);

    boolean existsByClientIdAndStartDateAfter(Long clientId, LocalDate date);

    Optional<GymMembership> findFirstByClientIdAndActiveFalseAndStartDateAfterOrderByStartDateAsc(Long id, LocalDate endDate);

    boolean existsByClientId(Long clientId);
}
