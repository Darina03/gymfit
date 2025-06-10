package daryna.gymfit.services;

import daryna.gymfit.dao.GroupClassRepository;
import daryna.gymfit.dto.ClassScheduleDto;
import daryna.gymfit.dto.GroupClassDto;
import daryna.gymfit.dto.GroupClassEnrollmentDto;
import daryna.gymfit.entities.*;
import jakarta.persistence.EntityNotFoundException;
import jdk.jfr.TransitionTo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class GroupClassService {

    private final GroupClassRepository groupClassRepository;
    private final ClientService clientService;


    public void save(GroupClass gc) {
        groupClassRepository.save(gc);
    }

    public List<GroupClassDto> getAllActiveAndNotFull(Principal principal) {
        List<GroupClass> classes;
        if (principal == null) {
            classes = groupClassRepository.findAllActiveAndNotFullClasses();
        } else {
            Client client = clientService.findByEmail(principal.getName());
            classes = groupClassRepository.findAllActiveAndNotFullClassesOrBelongsToClient(client.getId());
        }

        return classes.stream()
                .map(gc -> new GroupClassDto(
                        gc.getId(),
                        gc.getWorkoutAmount(),
                        gc.getPrice(),
                        gc.getMaxParticipants(),
                        gc.getClients().size(),
                        gc.getCoach().getName(),
                        gc.getCoach().getSurname(),
                        gc.getField().getName(),
                        gc.getClassSchedules().stream()
                                .sorted(Comparator
                                        .comparing(ClassSchedule::getScheduledDay)
                                        .thenComparing(ClassSchedule::getClassTime))
                                .map(cs -> new ClassScheduleDto(cs.getScheduledDay(), cs.getClassTime()))
                                .toList()
                ))
                .toList();
    }

    public boolean ifGymPassRequired(Long id) {
        return groupClassRepository.existsByIdAndIsGymMembershipRequiredTrue(id);
    }

    public GroupClass getGroupClassById(Long id) {
        return groupClassRepository.findById(id).orElse(null);
    }

    @Transactional
    public void unenroll(Long groupId, String email) {
        Client client = clientService.findByEmail(email);
        GroupClass gc = groupClassRepository.findById(groupId).orElseThrow(() -> new EntityNotFoundException("Group Class with id " + groupId + " not found"));
        gc.getClients().remove(client);
        if(gc.isFull()) gc.setFull(false);
        groupClassRepository.save(gc);
    }

    public List<GroupClass> findByCoach(Long coachId) {
        return groupClassRepository.findByCoachId(coachId);
    }
}
