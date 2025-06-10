package daryna.gymfit.dto;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;


public record CoachForCoachesPageDto(
        Long id,

        String name,

        String surname,

        String picUrl,

        Long idGym,

        List<String> fieldNames) {
}
