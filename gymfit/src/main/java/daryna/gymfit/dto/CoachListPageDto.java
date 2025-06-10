package daryna.gymfit.dto;

import daryna.gymfit.entities.Gym;

import java.util.List;

public record CoachListPageDto(

        List<CoachForCoachesPageDto> coaches,

        List<Gym> gyms,

        List<FieldDto> fields

) {
}
