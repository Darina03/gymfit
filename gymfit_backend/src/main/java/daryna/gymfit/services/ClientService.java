package daryna.gymfit.services;

import daryna.gymfit.dao.ClientRepository;
import daryna.gymfit.dto.ClientProfileDto;
import daryna.gymfit.dto.ClientUpdateDto;
import daryna.gymfit.dto.CoachForClientProfileDto;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.GroupClass;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public void save(Client client) {
        clientRepository.save(client);
    }

    public Client findByPhone(String phone) {
        return clientRepository.findByPhoneNumber(phone).orElse(null);
    }

    public Client getClientById(long id) {
        return clientRepository.findById(id)
                .orElse(null);
    }

    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void update(ClientUpdateDto client, String email) {
        Client clientDb=findByEmail(email);
        clientDb.setName(client.name());
        clientDb.setSurname(client.surname());
        clientDb.setBirthday(client.birthday());
        clientDb.setPhoneNumber(client.phoneNumber());
        clientRepository.save(clientDb);
    }

}
