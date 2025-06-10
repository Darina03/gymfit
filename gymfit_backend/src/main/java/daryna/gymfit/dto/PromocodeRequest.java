package daryna.gymfit.dto;

import java.util.List;

public record PromocodeRequest(

        String promocode,

        List<CartItemDto> cartItems) {}
