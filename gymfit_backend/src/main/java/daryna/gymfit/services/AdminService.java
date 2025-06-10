package daryna.gymfit.services;

import daryna.gymfit.dao.AdminRepository;
import daryna.gymfit.entities.Admin;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Admin findById(Long id) {
        return adminRepository.findById(id).orElse(null);
    }

    public void save(Admin admin) {
        adminRepository.save(admin);
    }

    public Admin findByEmail(String email) {
        return adminRepository.findByLogin(email).orElse(null);
    }
}
