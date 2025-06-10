package daryna.gymfit.dto;

import daryna.gymfit.entities.Field;
import daryna.gymfit.entities.enums.CoachRank;
import java.time.LocalDate;
import java.util.List;

public record CoachProfileDto(

        Long id,

        String name,

        String surname,

        String phoneNumber,

        LocalDate birthday,

        String email,

        String picUrl,

        String instagram,

        String motto,

        String description,

        int experience,

        double averageRating,

        CoachRank rank,

        List<String> fieldNames

        ) {
}
