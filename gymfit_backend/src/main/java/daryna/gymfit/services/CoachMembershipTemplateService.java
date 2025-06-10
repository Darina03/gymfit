package daryna.gymfit.services;

import daryna.gymfit.dao.CoachMembershipTemplateRepository;
import daryna.gymfit.dto.CoachMembershipTemplateDto;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.CoachMembershipTemplate;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CoachMembershipTemplateService {

    private final CoachMembershipTemplateRepository coachMembershipTemplateRepository;
    private final ClientService clientService;


    public List<CoachMembershipTemplateDto> getTemplatesForClient(Principal principal) {

        if (principal == null) {
            return coachMembershipTemplateRepository.findAvailableOnly();
        } else {
            Client client=clientService.findByEmail(principal.getName());
            return coachMembershipTemplateRepository.findAvailableOrBelongsToClient(client.getId());
        }
    }

    public CoachMembershipTemplate getCoachMembershipTemplateById(Long id){
        return coachMembershipTemplateRepository.findById(id).orElse(null);
    }

    public void save(CoachMembershipTemplate coachMembershipTemplate) {
        coachMembershipTemplateRepository.save(coachMembershipTemplate);
    }

    @Transactional
    public void markFullTrueForAllCoachTemplates(Long coachId) {
        List<CoachMembershipTemplate> templates=coachMembershipTemplateRepository.findAllByCoachId(coachId);

        for (CoachMembershipTemplate t: templates){
            t.setFull(false);
            coachMembershipTemplateRepository.save(t);
        }
    }

}
