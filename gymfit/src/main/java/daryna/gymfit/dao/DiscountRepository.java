package daryna.gymfit.dao;

import daryna.gymfit.dto.DiscountForDisplayingDto;
import daryna.gymfit.entities.Discount;
import org.apache.commons.io.file.PathUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount,Long> {

    List<Discount> findByIsActiveTrue();

    Optional<Discount> findByPromocodeIgnoreCase(String code);

    @Query("""
    SELECT new daryna.gymfit.dto.DiscountForDisplayingDto(
        d.title,
        d.description,
        d.startDate,
        d.endDate,
        d.imageUrl
    )
    FROM Discount d
    WHERE d.isActive = true AND d.isShown = true
""")
    List<DiscountForDisplayingDto> findAllIsActiveAndIsShown();
}
