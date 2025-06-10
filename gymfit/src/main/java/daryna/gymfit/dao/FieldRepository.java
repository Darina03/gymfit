package daryna.gymfit.dao;

import daryna.gymfit.entities.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {


    Set<Field> findAllByIdIn(List<Long> ids);
}
