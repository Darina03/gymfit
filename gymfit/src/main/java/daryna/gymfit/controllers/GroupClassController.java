package daryna.gymfit.controllers;

import daryna.gymfit.services.GroupClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/gymfit/group-classes")
@RequiredArgsConstructor
public class GroupClassController {

    private final GroupClassService groupClassService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllAvailableGroupClasses(Principal principal){
        return ResponseEntity.ok(groupClassService.getAllActiveAndNotFull(principal));
    }

    @PostMapping("/unenroll/{groupId}")
    public ResponseEntity<?> getAllAvailableGroupClasses(@PathVariable Long groupId, Principal principal){
        groupClassService.unenroll(groupId, principal.getName());
        return ResponseEntity.ok("");
    }
}
