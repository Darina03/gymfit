package daryna.gymfit.controllers;

import daryna.gymfit.dto.CoachMembershipTemplateDto;
import daryna.gymfit.services.CoachMembershipTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/gymfit/coach-membership-templates")
@RequiredArgsConstructor
public class CoachMembershipTemplateController {

    private final CoachMembershipTemplateService coachMembershipTemplateService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllTemplates(Principal principal) {
        List<CoachMembershipTemplateDto> templates = coachMembershipTemplateService.getTemplatesForClient(principal);
        return ResponseEntity.ok(templates);
    }
}
