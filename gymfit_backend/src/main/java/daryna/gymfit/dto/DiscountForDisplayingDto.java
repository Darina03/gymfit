package daryna.gymfit.dto;

import java.time.LocalDate;

public record DiscountForDisplayingDto(

        String title,

        String description,

        LocalDate startDate,

        LocalDate endDate,

        String imageUrl
) {
}
