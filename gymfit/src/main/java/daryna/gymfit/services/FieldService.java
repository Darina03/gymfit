package daryna.gymfit.services;

import daryna.gymfit.dao.FieldRepository;
import daryna.gymfit.dto.FieldDto;
import daryna.gymfit.entities.Field;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FieldService {

    private final FieldRepository fieldRepository;

    public List<FieldDto> findAll() {
        return fieldRepository.findAll()
                .stream()
                .map(f -> new FieldDto(f.getId(), f.getName()))
                .collect(Collectors.toList());
    }

}
