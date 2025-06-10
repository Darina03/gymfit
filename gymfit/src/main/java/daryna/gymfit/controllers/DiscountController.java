package daryna.gymfit.controllers;

import daryna.gymfit.dto.*;
import daryna.gymfit.entities.Client;
import daryna.gymfit.services.ClientService;
import daryna.gymfit.services.DiscountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/gymfit/discounts")
public class DiscountController {

    private final DiscountService discountService;
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<List<DiscountDto>> getApplicableDiscounts(@RequestBody List<CartItemDto> cartItems, Principal principal) {
        Client client = clientService.findByEmail(principal.getName());
        List<DiscountDto> applicableDiscounts = discountService.findApplicableDiscounts(cartItems, client);

        return ResponseEntity.ok(applicableDiscounts);
    }

    @PostMapping("/validate")
    public PromocodeResponse validatePromoCode(@RequestBody PromocodeRequest request,Principal principal) {
        Client client=clientService.findByEmail(principal.getName());
        return discountService.validatePromoCode(request.promocode(),request.cartItems(),client);
    }

    @GetMapping("/all")
    public ResponseEntity<List<DiscountForDisplayingDto>> getAllDiscounts(){
        return ResponseEntity.ok(discountService.findAllActive());
    }

}
