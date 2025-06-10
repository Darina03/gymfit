package daryna.gymfit.dto;

import java.time.LocalDate;

public record ClientUpdateDto(

        String name,

        String surname,

        LocalDate birthday,

        String phoneNumber
) {
}
