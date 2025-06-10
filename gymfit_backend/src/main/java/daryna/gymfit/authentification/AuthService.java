package daryna.gymfit.authentification;

import daryna.gymfit.dto.ClientRegistrationDto;
import daryna.gymfit.entities.Admin;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.Coach;
import daryna.gymfit.entities.enums.ClientAccountStatus;
import daryna.gymfit.services.AdminService;
import daryna.gymfit.services.ClientService;
import daryna.gymfit.services.CoachService;
import daryna.gymfit.services.GymService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientService clientService;
    private final CoachService coachService;
    private final AdminService adminService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest request) {
        String email = request.getEmail();
        String rawPassword = request.getPassword();

        Client client = clientService.findByEmail(email);
        if (client!=null){
            checkPassword(rawPassword, client.getPassword());
            String token = jwtService.generateToken(email, "CLIENT", client.getId());
            return new AuthResponse(token, "CLIENT");
        }

        Coach coach = coachService.findByEmail(email);
        if (coach!=null){
            checkPassword(rawPassword, coach.getPassword());
            String token = jwtService.generateToken(email, "COACH",coach.getId());
            return new AuthResponse(token, "COACH");
        }

        Admin admin = adminService.findByEmail(email);
        if (admin!=null){
            checkPassword(rawPassword, admin.getPassword());
            String token = jwtService.generateToken(email, "ADMIN", admin.getId());
            return new AuthResponse(token, "ADMIN");
        }

        throw new UsernameNotFoundException("User not found!");
    }

    private void checkPassword(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw new BadCredentialsException("Invalid credentials!");
        }
    }

    public AuthResponse register(ClientRegistrationDto request) {

        String encodedPassword = passwordEncoder.encode(request.password());


        if (clientService.findByEmail(request.email())!=null | clientService.findByPhone(request.phone())!=null) {
            throw new IllegalArgumentException("Client already exists");
        }
        Client client = new Client();
        client.setEmail(request.email());
        client.setPassword(encodedPassword);
        client.setName(request.firstName());
        client.setSurname(request.lastName());
        client.setBirthday(request.birthDate());
        client.setPhoneNumber(request.phone());
        client.setJoinDate(LocalDate.now());
        client.setStatus(ClientAccountStatus.ACTIVE);
        clientService.save(client);

        Client clientSaved=clientService.findByEmail(client.getEmail());

        String token = jwtService.generateToken(client.getEmail(), "CLIENT", clientSaved.getId());
        return new AuthResponse(token, "CLIENT");

    }
}

