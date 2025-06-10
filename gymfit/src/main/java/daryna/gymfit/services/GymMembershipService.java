package daryna.gymfit.services;

import com.stripe.exception.StripeException;
import daryna.gymfit.dao.GymMembershipRepository;
import daryna.gymfit.dto.GymMembershipDto;
import daryna.gymfit.dto.GymMembershipForBookingDto;
import daryna.gymfit.entities.*;
import daryna.gymfit.entities.enums.GymMembershipType;
import daryna.gymfit.entities.enums.WorkoutType;
import daryna.gymfit.payment.StripeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GymMembershipService {


    private final GymMembershipRepository gymMembershipRepository;
    private final ClientService clientService;


    public boolean hasActiveAllDayPass(String clientEmail) {

        return gymMembershipRepository.existsByClientEmailAndActiveTrue(clientEmail);

    }

    public void createGymAccess(Client client, GymMembershipType passType, int quantity, BigDecimal price) {
        LocalDate start = calculateNextMembershipStartDate(client.getId());

        for (int i = 0; i < quantity; i++) {
            GymMembership membership = new GymMembership();
            membership.setClient(client);
            membership.setPrice(price);
            membership.setType(passType);
            membership.setStartDate(start);
            membership.setEndDate(start.plusMonths(1).minusDays(1));

            gymMembershipRepository.save(membership);

            if (i == 0) {
                Optional<GymMembership> optMembership = findActive(client.getId());
                if (optMembership.isEmpty()) markActive(membership);
            }

            start = start.plusMonths(1);
        }

    }



    public LocalDate calculateNextMembershipStartDate(long clientId) {
        Optional<GymMembership> latestMembership = gymMembershipRepository.findTopByClientIdOrderByEndDateDesc(clientId);

        if (latestMembership.isPresent() && latestMembership.get().getEndDate().isAfter(LocalDate.now())) {
            // Last membership ends in future → start after that
            return latestMembership.get().getEndDate().plusDays(1);
        }

        return LocalDate.now();
    }


    public Optional<GymMembership> findActive(long clientId) {
        return gymMembershipRepository.findByActiveTrueAndClientId(clientId);
    }


    public void markInactiveWhenExpired(GymMembership gymMembership) {
        gymMembership.setActive(false);
        gymMembershipRepository.save(gymMembership);
    }


    public void markActive(GymMembership gymMembership) {
        gymMembership.setActive(true);
        gymMembershipRepository.save(gymMembership);
    }

    public GymMembershipDto getActiveGymMembership(long clientId){
        return gymMembershipRepository.getActiveGymMembershipDtoByClientId(clientId).orElse(null);
    }

    public GymMembershipForBookingDto getActiveGymMembershipByClientEmail(String email) {
        Client client=clientService.findByEmail(email);
        return gymMembershipRepository.findActiveByClientId(client.getId()).orElse(null);
    }

    public List<GymMembership> findExpiringOn(LocalDate date) {
        List<GymMembership> expiringToday = gymMembershipRepository.findByEndDateAndActive(date, true);

        return expiringToday.stream()
                .filter(membership -> !hasFutureMembership(membership.getClient().getId(), date))
                .collect(Collectors.toList());
    }

    private boolean hasFutureMembership(Long clientId, LocalDate afterDate) {
        return gymMembershipRepository.existsByClientIdAndStartDateAfter(clientId, afterDate);
    }

    @Transactional
    public void findExpiredAndMark(LocalDate date) {
        List<GymMembership> expired = gymMembershipRepository.findByEndDateAndActive(date, true);

        for (GymMembership membership : expired) {
            membership.setActive(false);

            Optional<GymMembership> next = gymMembershipRepository.findFirstByClientIdAndActiveFalseAndStartDateAfterOrderByStartDateAsc(
                    membership.getClient().getId(),
                    membership.getEndDate()
            );

            next.ifPresent(nextMembership -> nextMembership.setActive(true));
        }

        gymMembershipRepository.saveAll(expired);
    }

    public boolean existsByClient(Long clientId) {
        return gymMembershipRepository.existsByClientId(clientId);
    }
}