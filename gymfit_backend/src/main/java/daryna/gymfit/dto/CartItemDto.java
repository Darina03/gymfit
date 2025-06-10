package daryna.gymfit.dto;

import java.math.BigDecimal;

public record CartItemDto(

        Long id,

        String membershipType,

        String passType,

        int quantity,

        BigDecimal price
) {
}
