package daryna.gymfit.dto;



import java.util.List;

public record DiscountDto(

        String title,

        int discountPercent,

        List<Long> applicableItemIds
) { }
