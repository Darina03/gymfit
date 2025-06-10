package daryna.gymfit.dto;

import java.time.LocalDate;

public record ClientProfileDto(

        String name,

        String surname,

        String email,

        String phoneNumber,

        LocalDate birthday

) {
}
