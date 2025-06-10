package daryna.gymfit.dao;

import daryna.gymfit.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    public Admin findAdminByLoginAndPassword(String login, String password);

    public List<Admin> findAdminByGymNumber(int gymNumber);

    public Optional<Admin> findByLogin(String login);

}
