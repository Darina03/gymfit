package daryna.gymfit.dao;

import daryna.gymfit.dto.CoachForClientProfileDto;
import daryna.gymfit.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    public List<Client> findByNameAndSurname(String name, String surname);

    public Optional<Client> findByEmail(String email);

    public Optional<Client> findByPhoneNumber(String phone);

}
