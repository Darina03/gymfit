package daryna.gymfit.dao;

import daryna.gymfit.entities.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {

    public Gym findByNumber(int number);
}
