package daryna.gymfit.dao;

import daryna.gymfit.dto.GroupClassDto;
import daryna.gymfit.dto.GroupClassEnrollmentDto;
import daryna.gymfit.entities.GroupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupClassRepository extends JpaRepository<GroupClass, Long> {

    @Query("SELECT gc FROM GroupClass gc " +
            "LEFT JOIN FETCH gc.classSchedules cs " +
            "WHERE gc.isActive = true AND gc.isFull = false")
    List<GroupClass> findAllActiveAndNotFullClasses();


    @Query("""
                SELECT gc FROM GroupClass gc
                LEFT JOIN FETCH gc.classSchedules cs
                WHERE gc.isActive = true
                  AND (
                    gc.isFull = false
                    OR EXISTS (
                        SELECT 1
                        FROM gc.clients cl
                        WHERE cl.id = :clientId
                    )
                )
            """)
    List<GroupClass> findAllActiveAndNotFullClassesOrBelongsToClient(@Param("clientId") Long clientId);

    boolean existsByIdAndIsGymMembershipRequiredTrue(Long id);


    List<GroupClass> findByCoachId(Long coachId);
}
