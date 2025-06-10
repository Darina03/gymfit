package daryna.gymfit.controllers;

import daryna.gymfit.dto.FieldDto;
import daryna.gymfit.entities.Field;
import daryna.gymfit.services.FieldService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/gymfit/fields")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @GetMapping("/all")
    public List<FieldDto> getAll(){
        return fieldService.findAll();
    }
}
