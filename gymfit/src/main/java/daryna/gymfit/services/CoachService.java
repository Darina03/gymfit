package daryna.gymfit.services;

import daryna.gymfit.dto.*;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.GroupClass;
import daryna.gymfit.entities.enums.CoachAccountStatus;
import daryna.gymfit.entities.enums.CoachRank;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import daryna.gymfit.dao.CoachRepository;
import daryna.gymfit.entities.Coach;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CoachService {

    private final CoachRepository coachRepository;
    private final ClientService clientService;
    private final CoachMembershipTemplateService coachMembershipTemplateService;

    public List<CoachForCoachesPageDto> getCoachesForCoachesPage() {
        List<Object[]> results = coachRepository.findCoachesWithFields();

        return results.stream().map(result -> {
            Long id = (Long) result[0];
            String name = (String) result[1];
            String surname = (String) result[2];
            String picUrl = (String) result[3];
            Long idGym = (Long) result[4];
            List<String> fieldNames = Arrays.asList(((String) result[5]).split(", "));

            return new CoachForCoachesPageDto(id, name, surname, picUrl, idGym, fieldNames);
        }).collect(Collectors.toList());
    }

    public CoachProfileDto getCoachForProfile(Long id) {
        Object[] result = (Object[]) coachRepository.findCoachInfo(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach with " + id+ " not found"));
        return new CoachProfileDto(
                ((Number) result[0]).longValue(),
                (String) result[1],
                (String) result[2],
                (String) result[3],
                ((java.sql.Date) result[4]).toLocalDate(),
                (String) result[5],
                (String) result[6],
                (String) result[7],
                (String) result[8],
                (String) result[9],
                ((Number) result[10]).intValue(),
                ((Number) result[11]).doubleValue(),
                CoachRank.valueOf((String) result[12]),
                Arrays.asList(((String) result[13]).split(",\\s*"))
        );
    }

    public Coach findByEmail(String email) {
        return coachRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void save(Coach coach){
        coachRepository.save(coach);
    }


    public Coach getCoachById(Long id) {
        return coachRepository.findById(id)
                .orElse(null);
    }

    @Transactional
    public void unenrollClient(Long coachId, String email){
        Client client = clientService.findByEmail(email);
        Coach coach = getCoachById(coachId);
        coach.getClients().remove(client);
        coachMembershipTemplateService.markFullTrueForAllCoachTemplates(coachId);
        coachRepository.save(coach);
    }


    public List<Coach> getAllActiveCoaches() {
        return coachRepository.findByStatus(CoachAccountStatus.ACTIVE);
    }

}
