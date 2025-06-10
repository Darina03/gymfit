package daryna.gymfit.services;

import daryna.gymfit.dao.MembershipRepository;
import daryna.gymfit.dto.*;
import daryna.gymfit.entities.*;
import daryna.gymfit.entities.enums.WorkoutType;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final CoachMembershipTemplateService coachMembershipTemplateService;
    private final GroupClassService groupClassService;
    private final ClientService clientService;
    private final CoachService coachService;
    private final WorkoutClientsService workoutClientsService;

    @Transactional
    public void createCoachMembership(Client client, long templateId, int quantity) {

        CoachMembershipTemplate coachMembershipTemplate = coachMembershipTemplateService.getCoachMembershipTemplateById(templateId);

        Coach coach = coachMembershipTemplate.getCoach();

        if (!coach.getClients().contains(client)) {
            coach.getClients().add(client);
            client.getCoaches().add(coach);
            if (coach.getClients().size() == coach.getPersonalClientsLimit()) coachMembershipTemplate.setFull(true);
        }


        LocalDate start = calculateNextMembershipStartDate(client.getId(), templateId, WorkoutType.PERSONAL);

        for (int i = 0; i < quantity; i++) {
            Membership membership = new Membership();
            membership.setClient(client);
            membership.setType(WorkoutType.PERSONAL);
            membership.setCoachMembershipTemplate(coachMembershipTemplate);
            membership.setWorkoutAmountLeft(coachMembershipTemplate.getWorkoutAmount());
            membership.setStartDate(start);
            membership.setEndDate(start.plusWeeks(4).minusDays(1));

            membershipRepository.save(membership);

            if (i == 0) {
                Optional<Membership> optMembership = findActiveForCoach(client.getId(), templateId, WorkoutType.PERSONAL);
                if (optMembership.isEmpty()) markActive(membership);
            }

            start = start.plusMonths(1);
        }

        coachService.save(coach);
//        clientService.save(client);
        coachMembershipTemplateService.save(coachMembershipTemplate);

    }

    @Transactional
    public void createGroupMembership(Client client, long templateId, int quantity) {

        GroupClass groupClass = groupClassService.getGroupClassById(templateId);

        if (!groupClass.getClients().contains(client)) {
            groupClass.getClients().add(client);
            if (groupClass.getClients().size() == groupClass.getMaxParticipants()) groupClass.setFull(true);
        }


        LocalDate start = calculateNextMembershipStartDate(client.getId(), templateId, WorkoutType.GROUP);

        for (int i = 0; i < quantity; i++) {
            Membership membership = new Membership();
            membership.setClient(client);
            membership.setType(WorkoutType.GROUP);
            membership.setGroupClass(groupClass);
            membership.setWorkoutAmountLeft(groupClass.getWorkoutAmount());
            membership.setStartDate(start);
            membership.setEndDate(start.plusWeeks(4).minusDays(1));

            membershipRepository.save(membership);
            workoutClientsService.createGroupWorkoutClients(membership);

            if (i == 0) {
                Optional<Membership> optMembership = findActiveForCoach(client.getId(), templateId, WorkoutType.GROUP);
                if (optMembership.isEmpty()) markActive(membership);
            }

            start = start.plusMonths(1);
        }

        groupClassService.save(groupClass);

    }

    public LocalDate calculateNextMembershipStartDate(long clientId, long templateId, WorkoutType workoutType) {
        Optional<Membership> latestMembership = Optional.empty();
        if (workoutType == WorkoutType.GROUP) {
            latestMembership = membershipRepository
                    .findTopByClientIdAndGroupClassIdOrderByEndDateDesc(clientId, templateId);
        } else if (workoutType == WorkoutType.PERSONAL) {
            latestMembership = membershipRepository
                    .findTopByClientIdAndCoachMembershipTemplateIdOrderByEndDateDesc(clientId, templateId);
        }

        if (latestMembership.isPresent() && latestMembership.get().getEndDate().isAfter(LocalDate.now())) {
            return latestMembership.get().getEndDate().plusDays(1);
        }

        return LocalDate.now();
    }

    public Optional<Membership> findActiveForCoach(long clientId, long templateId, WorkoutType workoutType) {
        Optional<Membership> activeMembership = Optional.empty();
        if (workoutType == WorkoutType.GROUP) {
            activeMembership = membershipRepository
                    .findByIsActiveTrueAndClientIdAndGroupClassId(clientId, templateId);
        } else if (workoutType == WorkoutType.PERSONAL) {
            activeMembership = membershipRepository
                    .findByIsActiveTrueAndClientIdAndCoachMembershipTemplateId(clientId, templateId);
        }
        return activeMembership;
    }


    @Transactional
    public void markInactiveWhenExpired(Membership membership) {
        membership.setActive(false);
        membershipRepository.save(membership);
    }

    @Transactional
    public void markActive(Membership membership) {
        membership.setActive(true);
        membershipRepository.save(membership);
    }

    public List<MembershipDto> getActiveMembershipsByWorkoutType(Long clientId, WorkoutType type) {
        if (type == WorkoutType.PERSONAL)
            return membershipRepository.getPersonalMemberships(clientId);
        else
            return membershipRepository.getGroupMemberships(clientId);
    }


    public List<MembershipForBookingDto> findActiveCoachMembershipsForClient(String email) {
        Client client = clientService.findByEmail(email);
        return membershipRepository.getActiveMembershipsForBookingPage(client.getId());
    }

    public Membership findActiveForClientByCoachIdAndFieldId(Long clientId, Long coachId, Long fieldId) {
        return membershipRepository.findActiveForClientByCoachIdAndFieldId(clientId, coachId, fieldId).orElseThrow(() -> new EntityNotFoundException("Membership not found!"));
    }

    public void save(Membership membership) {
        membershipRepository.save(membership);
    }

    public Membership findById(long membershipId) {
        return membershipRepository.findById(membershipId).orElseThrow(() -> new EntityNotFoundException("Membership not found!"));
    }

    public List<CoachEnrollmentMembershipDto> getPersonalCoachEnrollments(Long clientId) {
        List<Object[]> raw = membershipRepository.getPersonalCoachEnrollments(clientId);
        return raw.stream()
                .map(row -> new CoachEnrollmentMembershipDto(
                        ((Timestamp) row[0]).toLocalDateTime().toLocalDate(),
                        new CoachForClientProfileDto(
                                (Long) row[1],
                                (String) row[2],
                                (String) row[3],
                                (String) row[4]
                        )
                ))
                .toList();
    }

    public List<GroupClassEnrollmentMembershipDto> getEnrolledClasses(Long clientId) {
        List<Object[]> raw = membershipRepository.getGroupClassEnrollments(clientId);

        return raw.stream()
                .map(row -> new GroupClassEnrollmentMembershipDto(
                        ((Timestamp) row[0]).toLocalDateTime().toLocalDate(),
                        new GroupClassEnrollmentDto(
                                ((Number) row[1]).longValue(),
                                (String) row[2],
                                (String) row[3],
                                (String) row[4]
                        )
                ))
                .toList();
    }

    public List<Membership> findExpiringOn(LocalDate date) {
        List<Membership> expiringToday = membershipRepository.findByEndDateAndIsActive(date, true);

        return expiringToday.stream()
                .filter(membership -> !hasFutureMembership(membership.getClient().getId(), membership.getType(), date))
                .collect(Collectors.toList());
    }

    private boolean hasFutureMembership(Long clientId, WorkoutType type, LocalDate afterDate) {
        return membershipRepository.existsByClientIdAndTypeAndStartDateAfter(clientId, type, afterDate);
    }

    @Transactional
    public void findExpiredAndMark(LocalDate date) {
        List<Membership> expired = membershipRepository.findByEndDateAndIsActive(date, true);

        for (Membership membership : expired) {
            membership.setActive(false);

            Optional<Membership> next = membershipRepository.findFirstByClientIdAndTypeAndIsActiveFalseAndStartDateAfterOrderByStartDateAsc(
                    membership.getClient().getId(),
                    membership.getType(),
                    membership.getEndDate()
            );

            next.ifPresent(nextMembership -> nextMembership.setActive(true));
        }

        membershipRepository.saveAll(expired);
    }

    public boolean existsByClient(Long clientId) {
        return membershipRepository.existsByClientId(clientId);
    }
}

