package daryna.gymfit.controllers;

import daryna.gymfit.services.GymService;
import daryna.gymfit.entities.Gym;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gymfit/gyms")
@AllArgsConstructor
public class GymController {

    private final GymService gymService;

    @GetMapping("/all")
    public List<Gym> getAll(){
        return gymService.findAll();
    }
}
