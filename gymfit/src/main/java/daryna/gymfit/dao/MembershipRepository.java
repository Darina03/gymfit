package daryna.gymfit.dao;

import daryna.gymfit.dto.*;
import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.enums.WorkoutType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByClientId(Long clientId);

    List<Membership> findByEndDate(LocalDate date);


    @Query("""
                SELECT new daryna.gymfit.dto.MembershipDto(
                    cmt.field.name,
                    cmt.coach.name,
                    cmt.coach.surname,
                    cmt.workoutAmount,
                    m.workoutAmountLeft,
                    m.endDate,
                    m.type
                )
                FROM Membership m
                JOIN m.coachMembershipTemplate cmt
                WHERE m.client.id = :clientId
                  AND m.type = 'PERSONAL'
                  AND m.isActive = true
            """)
    List<MembershipDto> getPersonalMemberships(@Param("clientId") long clientId);

    @Query("""
                SELECT new daryna.gymfit.dto.MembershipDto(
                    gc.field.name,
                    gc.coach.name,
                    gc.coach.surname,
                    gc.workoutAmount,
                    m.workoutAmountLeft,
                    m.endDate,
                    m.type
                )
                FROM Membership m
                JOIN m.groupClass gc
                WHERE m.client.id = :clientId
                  AND m.type = 'GROUP'
                  AND m.isActive = true
            """)
    List<MembershipDto> getGroupMemberships(@Param("clientId") long clientId);


    Optional<Membership> findTopByClientIdAndGroupClassIdOrderByEndDateDesc(long clientId, long templateId);

    Optional<Membership> findTopByClientIdAndCoachMembershipTemplateIdOrderByEndDateDesc(long clientId, long templateId);

    Optional<Membership> findByIsActiveTrueAndClientIdAndGroupClassId(long clientId, long templateId);

    Optional<Membership> findByIsActiveTrueAndClientIdAndCoachMembershipTemplateId(long clientId, long templateId);

    @Query("""
                SELECT new daryna.gymfit.dto.MembershipForBookingDto(
                    m.id,
                    m.endDate,
                    m.coachMembershipTemplate.field.name,
                    new daryna.gymfit.dto.CoachForBookingWorkoutDto(
                        m.coachMembershipTemplate.coach.id,
                        m.coachMembershipTemplate.coach.name,
                        m.coachMembershipTemplate.coach.surname
                    )
                )
                FROM Membership m
                WHERE m.client.id = :clientId
                  AND m.isActive = true
                  AND m.type = 'PERSONAL'
            """)
    List<MembershipForBookingDto> getActiveMembershipsForBookingPage(@Param("clientId") Long clientId);

    @Query("""
            SELECT m FROM Membership m
            WHERE m.client.id = :clientId
              AND m.isActive = true
              AND m.coachMembershipTemplate.coach.id = :coachId
              AND m.coachMembershipTemplate.field.id = :fieldId
            """)
    Optional<Membership> findActiveForClientByCoachIdAndFieldId(
            @Param("clientId") Long clientId,
            @Param("coachId") Long coachId,
            @Param("fieldId") Long fieldId
    );

    Optional<Membership> findById(long membershipId);

    @Query(value = """
    SELECT DISTINCT ON (c.id)
        m.end_date + INTERVAL '8 days',
        c.id,
        c.name,
        c.surname,
        c.pic_url
    FROM memberships m
    JOIN coach_membership_templates t ON m.id_coach_membership_template = t.id
    JOIN coaches c ON t.id_coach = c.id
    JOIN coach_clients cc ON cc.id_coach = c.id AND cc.id_client = m.id_client
    WHERE m.id_client = :clientId
      AND (m.end_date >= CURRENT_DATE - INTERVAL '7 days' OR m.start_date > CURRENT_DATE)
    ORDER BY c.id, m.end_date DESC
    """, nativeQuery = true)
    List<Object[]> getPersonalCoachEnrollments(@Param("clientId") Long clientId);

    @Query(value = """
              SELECT DISTINCT ON (gct.id)
                  m.end_date + INTERVAL '8 days',
                  gct.id,
                  f.name,
                  c.name,
                  c.surname
              FROM memberships m
              JOIN classes gct ON m.id_class = gct.id
              JOIN fields f on gct.id_field = f.id
              JOIN coaches c ON gct.id_coach = c.id
              JOIN class_enrollments ce ON ce.id_class = gct.id AND ce.id_client = m.id_client
              WHERE m.id_client = :clientId
                    AND (m.end_date >= CURRENT_DATE - INTERVAL '7 days' OR m.start_date > CURRENT_DATE)
              ORDER BY gct.id, m.end_date DESC
              """, nativeQuery = true)
    List<Object[]> getGroupClassEnrollments(@Param("clientId") Long clientId);

    List<Membership> findByEndDateAndIsActive(LocalDate endDate, boolean isActive);

    boolean existsByClientIdAndTypeAndStartDateAfter(Long clientId, WorkoutType type, LocalDate date);

    Optional<Membership> findFirstByClientIdAndTypeAndIsActiveFalseAndStartDateAfterOrderByStartDateAsc(Long id, WorkoutType type, LocalDate endDate);

    boolean existsByClientId(Long clientId);
}
