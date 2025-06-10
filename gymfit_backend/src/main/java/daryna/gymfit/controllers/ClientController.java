package daryna.gymfit.controllers;

import daryna.gymfit.dto.*;
import daryna.gymfit.entities.Client;
import daryna.gymfit.entities.GymMembership;
import daryna.gymfit.entities.Membership;
import daryna.gymfit.entities.enums.WorkoutType;
import daryna.gymfit.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/gymfit/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final MembershipService membershipService;
    private final GymMembershipService gymMembershipService;


    @GetMapping("/profile-info")
    public ResponseEntity<ClientDashboardDto> getClientDashboard(Principal principal) {

        Client client = clientService.findByEmail(principal.getName());

        ClientProfileDto clientDto = new ClientProfileDto(
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getBirthday());
        GymMembershipDto gym = gymMembershipService.getActiveGymMembership(client.getId());
        List<MembershipDto> personal =membershipService.getActiveMembershipsByWorkoutType(client.getId(), WorkoutType.PERSONAL);
        List<MembershipDto> group = membershipService.getActiveMembershipsByWorkoutType(client.getId(), WorkoutType.GROUP);


        List<CoachEnrollmentMembershipDto> personalCoaches = membershipService.getPersonalCoachEnrollments(client.getId());
        List<GroupClassEnrollmentMembershipDto> enrolledGroups = membershipService.getEnrolledClasses(client.getId());

        ClientDashboardDto dto = new ClientDashboardDto(
                clientDto,
                gym,
                personal,
                group,
                personalCoaches,
                enrolledGroups
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/profile-update")
    public ResponseEntity<ClientProfileDto> getClientForUpdate(Principal principal) {

        Client client = clientService.findByEmail(principal.getName());

        ClientProfileDto clientDto = new ClientProfileDto(
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getPhoneNumber(),
                client.getBirthday());

        return ResponseEntity.ok(clientDto);
    }

    @PostMapping("/profile-update")
    public ResponseEntity<String> updateClient(@RequestBody ClientUpdateDto client, Principal principal) {
        clientService.update(client,principal.getName());
        return ResponseEntity.ok("");
    }


}
