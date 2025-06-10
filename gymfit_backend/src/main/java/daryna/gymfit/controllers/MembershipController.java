package daryna.gymfit.controllers;

import daryna.gymfit.dto.GymAndCoachMembershipsDtoForBooking;
import daryna.gymfit.dto.GymMembershipForBookingDto;
import daryna.gymfit.dto.MembershipForBookingDto;
import daryna.gymfit.services.GymMembershipService;
import daryna.gymfit.services.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gymfit/memberships/")
public class MembershipController {

    private final MembershipService membershipService;
    private  final GymMembershipService gymMembershipService;

    @GetMapping("/all-active-for-booking")
    public ResponseEntity<GymAndCoachMembershipsDtoForBooking> getAllCoachesForBooking(Principal principal){
        List<MembershipForBookingDto> coachMemberships=membershipService.findActiveCoachMembershipsForClient(principal.getName());
        GymMembershipForBookingDto gymMembership = gymMembershipService.getActiveGymMembershipByClientEmail(principal.getName());
        return ResponseEntity.ok(new GymAndCoachMembershipsDtoForBooking(coachMemberships,gymMembership));
    }


}
